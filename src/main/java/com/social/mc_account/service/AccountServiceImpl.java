package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
import com.social.mc_account.feign.StorageClient;
import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
import com.social.mc_account.repository.AccountRepository;
import com.social.mc_account.security.JwtUtils;
import com.social.mc_account.specification.AccountSpecification;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final JwtUtils jwtUtils;
    private final StorageClient storageClient;
    private final KafkaProducer producer;

    private final Counter failedAuthCounter; //кастомные счётчики
    private final MeterRegistry meterRegistry;


    @Override
    public AccountDataDTO getDataAccount(String authorization, String email) {
        Account account = accountRepository.findByEmail(email);

        if (account != null) {
            AccountDataDTO accountDataDTO = mapper.toAccountDataDtoFromAccount(account);
            log.info("The account: {} was successfully found by email: {}", account, email);
            return accountDataDTO;
        } else {
            log.warn("The account with email: {} not found", email);
            failedAuthCounter.increment();
            throw new ResourceNotFoundException("The account with email: " + email + " not found");
        }
    }

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Account account = mapper.toAccountFromAccountMeDto(accountMeDTO);

        account.setUpdateOn(LocalDateTime.now());

        accountRepository.save(account);

        RegistrationDto accountDtoRequest = RegistrationDto.builder()
                .uuid(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .build();

        producer.sendMessageForAuth(accountDtoRequest);

        log.info("The account: {} was successfully updated and message sent to Kafka", account);
        return accountMeDTO;
    }

    @Override
    public AccountMeDTO createAccount(RegistrationDto registrationDto) {
        try {
            Optional<Account> existingAccount = Optional.ofNullable(accountRepository.findByEmail(registrationDto.getEmail()));

            if (existingAccount.isPresent()) {
                log.warn("Account with email {} already exists", registrationDto.getEmail());
                throw new IllegalArgumentException("Account with email " + registrationDto.getEmail() + " already exists");
            }

            Account account = Account.builder()
                    .id(registrationDto.getUuid())
                    .password(registrationDto.getPassword1())
                    .isDeleted(registrationDto.isDeleted())
                    .email(registrationDto.getEmail())
                    .firstName(registrationDto.getFirstName())
                    .lastName(registrationDto.getLastName())
                    .role(registrationDto.getRole())
                    .regDate(registrationDto.getReg_date())
                    .build();

            accountRepository.save(account);
            log.info("Account saved with UUID: " + account.getId());

            AccountMeDTO accountMeDTO = mapper.toAccountMeDtoForAccount(account);
            log.info("Account successfully created from Kafka message!");
            return accountMeDTO;
        } catch (Exception e) {
            log.error("Error creating account from RegistrationDto: {}", registrationDto, e);
            throw e;
        }
    }

    @Override
    public AccountMeDTO getDataMyAccount(String authorization) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        log.info("Searching for account with UUID: " + id);

        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account data with id: {} has been successfully received", account.getId());
            return mapper.toAccountMeDtoForAccount(account);
        }
        log.warn("The account with id: {} not found", id);
        throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

    public AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account existingAccount = optionalAccount.get();
            Account updatedAccount = mapper.toAccountFromAccountMeDto(accountMeDTO);

            updatedAccount.setId(id);
            updatedAccount.setEmail(jwtUtils.getEmail(authorization));
            updatedAccount.setPassword(existingAccount.getPassword());
            updatedAccount.setRole(existingAccount.getRole());
            updatedAccount.setDeleted(existingAccount.isDeleted());
            updatedAccount.setRegDate(existingAccount.getRegDate());

            boolean isEmailOrRoleChanged =
                    !existingAccount.getEmail().equals(updatedAccount.getEmail()) ||
                            !existingAccount.getRole().equals(updatedAccount.getRole());

            if (!existingAccount.equals(updatedAccount)) {

                updatedAccount.setUpdateOn(LocalDateTime.now());

                accountRepository.save(updatedAccount);

                if (isEmailOrRoleChanged) {
                    RegistrationDto accountDtoRequest = RegistrationDto.builder()
                            .uuid(updatedAccount.getId())
                            .email(updatedAccount.getEmail())
                            .role(updatedAccount.getRole())
                            .build();
                    producer.sendMessageForAuth(accountDtoRequest);
                }

                log.info("The authorized account: {} successfully updated", updatedAccount);
                return mapper.toAccountMeDtoForAccount(updatedAccount);
            } else {
                log.info("No changes detected for account: {}", existingAccount);
                return mapper.toAccountMeDtoForAccount(existingAccount);
            }
        } else {
            log.warn("The account with id: {} not found", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not found");
        }
    }

    @Override
    public void deleteAccount(String authorization) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        Optional<Account> optionalUser = accountRepository.findById(id);

        if (optionalUser.isPresent()) {
            Account account = optionalUser.get();
            account.setDeleted(true);
            accountRepository.save(account);
            log.info("The account with id: {} successfully softly deleted", account.getId());

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(() -> {
                accountRepository.delete(account);
                log.info("The account with id: {} completely deleted from the database", account.getId());
            }, 10, TimeUnit.DAYS);
        } else {
            log.warn("The account with id: {} not found", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not found");
        }
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void putNotification() {
        List<NotificationDTO> birthdayDTOs = accountRepository.findAll().stream()
                .filter(account -> LocalDate.now().equals(account.getBirthDate()))
                .map(account -> NotificationDTO.builder()
                        .id(UUID.randomUUID())
                        .authorId(account.getId())
                        .content("С Днём Рождения!")
                        .notificationType(NotificationType.BIRTHDAY)
                        .sentTime(LocalDateTime.now())
                        .serviceName(MicroServiceName.ACCOUNT)
                        .eventId(UUID.randomUUID())
                        .isReaded(false)
                        .build())
                .toList();

        birthdayDTOs.forEach(producer::sendMessageForNotification);
    }

    @Override
    public AccountMeDTO getDataById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        log.info("Searching for account with UUID: " + id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account with id: {} was successfully found", id);
            return mapper.toAccountMeDtoForAccount(account);
        }
        log.warn("The account with id: {} not found", id);
        throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

    @Override
    public void deleteAccountById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account with id: {} successfully deleted", id);
            accountRepository.delete(account);
        } else {
            log.warn("The account with id: {} not found", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not found");
        }
    }

    @Override
    public StatisticDTO getStatistic(StatisticRequestDTO statisticRequestDTO) {
        StatisticDTO statistics = new StatisticDTO();
        CountPerAgeDTO countPerAgeDTO = new CountPerAgeDTO();
        CountPerMonthDTO countPerMonthDTO = new CountPerMonthDTO();

        List<Account> allAccounts = accountRepository.findAll();
        LocalDate firstMonth = statisticRequestDTO.getFirstMonth();
        LocalDate lastMonth = statisticRequestDTO.getLastMonth();
        LocalDate birthDate = statisticRequestDTO.getDate();
        LocalDate currentDate = LocalDate.now();

        int age = Period.between(birthDate, currentDate).getYears();
        int countPerAge = 0;
        int countPerMonth = 0;

        for (Account account : allAccounts) {
            if (account.getBirthDate() != null && Period.between(account.getBirthDate(), currentDate).getYears() == age) {
                countPerAge++;
            }

            if (account.getRegDate() != null &&
                    !account.getRegDate().isBefore(firstMonth) &&
                    !account.getRegDate().isAfter(lastMonth)) {
                countPerMonth++;
            }
        }

        statistics.setDate(birthDate);
        countPerAgeDTO.setAge(age);
        countPerAgeDTO.setCount(countPerAge);
        countPerMonthDTO.setCount(countPerMonth);

        statistics.setCount(countPerMonth);
        statistics.setCountPerAgeDTO(countPerAgeDTO);
        statistics.setCountPerMonthDTO(countPerMonthDTO);

        log.info("statistic received");
        return statistics;
    }

    @Override
    public AccountPageDTO getListAccounts(SearchDTO searchDTO, Page pageDto) {
        log.info("Starting getListAccounts with SearchDTO: {}", searchDTO);
        log.info("Page information: page = {}, size = {}, sort = {}", pageDto.getPage(), pageDto.getSize(), pageDto.getSort());

        Sort sort = Sort.unsorted();

        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sort);

        log.info("Pageable created: page = {}, size = {}", pageable.getPageNumber(), pageable.getPageSize());

        org.springframework.data.domain.Page<Account> accountsPage = accountRepository.findAll(AccountSpecification.findWithFilter(searchDTO), pageable);

        if (accountsPage == null) {
            log.error("AccountsPage is null");
            throw new IllegalStateException("Page cannot be null");
        }

        List<Account> accounts = accountsPage.getContent();

        log.info("Number of accounts found: {}", accounts.size());
        accounts.forEach(account -> log.info("Account: id = {}, firstName = {}, lastName = {}", account.getId(), account.getFirstName(), account.getLastName()));

        int totalPages = accountsPage.getTotalPages();
        long totalElements = accountsPage.getTotalElements();
        int numberOfElements = accountsPage.getNumberOfElements();

        SortDTO sortDTO = SortDTO.builder()
                .unsorted(sort.isUnsorted())
                .sorted(sort.isSorted())
                .empty(sort.isEmpty())
                .build();

        PageableDTO pageableDTO = PageableDTO.builder()
                .sortDTO(sortDTO)
                .unpaged(pageable.isUnpaged())
                .paged(pageable.isPaged())
                .pageSize(pageable.getPageSize())
                .pageNumber(pageable.getPageNumber())
                .offset((int) pageable.getOffset())
                .build();

        boolean isFirst = accountsPage.isFirst();
        boolean isLast = accountsPage.isLast();
        int size = accounts.size();
        int number = accountsPage.getNumber();
        boolean empty = accountsPage.isEmpty();

        log.info("Pagination details: totalPages = {}, totalElements = {}, numberOfElements = {}, isFirst = {}, isLast = {}, size = {}, number = {}, empty = {}",
                totalPages, totalElements, numberOfElements, isFirst, isLast, size, number, empty);

        AccountPageDTO result = AccountPageDTO.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .sortDTO(sortDTO)
                .numberOfElements(numberOfElements)
                .pageable(pageableDTO)
                .first(isFirst)
                .last(isLast)
                .size(size)
                .content(mapper.toAccountsMeDtoForAccounts(accounts))
                .number(number)
                .empty(empty)
                .build();

        log.info("AccountPageDTO built: {}", result);

        return result;
    }

    @Override
    public List<UUID> getListIdsByFirstNameAndLastName(SearchDTO searchDTO) {
        List<Account> accounts = accountRepository.findAll(AccountSpecification.findWithFilter(searchDTO));
        return accounts.stream().map(Account::getId).toList();
    }
}