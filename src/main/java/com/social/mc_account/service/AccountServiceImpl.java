package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
import com.social.mc_account.repository.AccountRepository;
import com.social.mc_account.security.JwtUtils;
import com.social.mc_account.specification.AccountSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
@ConditionalOnProperty(name = "scheduler.enabled", matchIfMissing = true)
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private final AccountMapper mapper;
    private final JwtUtils jwtUtils;
    private final KafkaProducer producer;

    @Override
    public AccountDataDTO getDataAccount(String authorization, String email) {
        Account account = accountRepository.findByEmail(email);
        AccountDataDTO accountDataDTO = mapper.toAccountDataDtoFromAccount(account);
        if (account != null) {
            log.info("The account: {} was successfully found by email: {}", account, email);
            return accountDataDTO;
        } else {
            log.warn("The account with email: {} not found", email);
            throw new ResourceNotFoundException("The account with email: " + email + " not found");
        }
    }

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Account account = mapper.toAccountFromAccountMeDto(accountMeDTO);
        account.setUpdatedOn(LocalDate.now());
        accountRepository.save(account);

        AccountDtoRequest accountDtoRequest = AccountDtoRequest.builder()
                .id(account.getId())
                .email(account.getEmail())
                .role(account.getRole())
                .build();

        producer.sendMessageForAuth(accountDtoRequest);

        log.info("The account: {} was successfully updated and message sent to Kafka", account);
        return accountMeDTO;
    }

    @Transactional
    @Override
    public AccountMeDTO createAccount(AccountDtoRequest accountDtoRequest) {
        Account account = Account.builder()
                .id(accountDtoRequest.getId())
                .email(accountDtoRequest.getEmail())
                .role(accountDtoRequest.getRole())
                .build();

        accountRepository.save(account);

        AccountMeDTO accountMeDTO = mapper.toAccountMeDtoForAccount(account);
        log.info("Account successfully created from Kafka message!");
        return accountMeDTO;
    }


    @Override
    public AccountMeDTO getDataMyAccount(String authorization) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Thea account data with id: {} has been successfully received", account.getId());
            return mapper.toAccountMeDtoForAccount(account);
        }
             log.warn("The account with id: {} not found", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

    @Override
    public AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = mapper.toAccountFromAccountMeDto(accountMeDTO);
            accountRepository.save(account);

            AccountDtoRequest accountDtoRequest = AccountDtoRequest.builder()
                    .id(account.getId())
                    .email(account.getEmail())
                    .role(account.getRole())
                    .build();
            producer.sendMessageForAuth(accountDtoRequest);

            log.info("The authorize account: {} successfully updated", account);
            return mapper.toAccountMeDtoForAccount(account);
        } else {
            log.warn("The account with id: {} not updated", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not updated");
        }
    }

    @Override
    @Async
    public void deleteAccount(String authorization) throws InterruptedException {
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
                .filter(account -> LocalDate.now().equals(account.getBirth_date()))
                .map(account -> {
                    NotificationDTO birthdayAccount = NotificationDTO.builder()
                            .id(UUID.randomUUID())
                            .authorId(account.getId())
                            .content("С Днём Рождения!")
                            .notificationType(NotificationType.BIRTHDAY)
                            .sentTime(LocalDateTime.now())
                            .serviceName(MicroServiceName.ACCOUNT)
                            .eventId(UUID.randomUUID())
                            .isReaded(false)
                            .build();
                    return birthdayAccount;
                })
                .collect(Collectors.toList());

        for (NotificationDTO birthdayDTO : birthdayDTOs) {
            producer.sendMessageForNotification(birthdayDTO);
        }
    }


    @Override
    public AccountDataDTO getDataById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);

        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account with id: {} was successfully found", id);
            return mapper.toAccountDataDtoFromAccount(account);
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
            if (account.getBirth_date() != null && Period.between(account.getBirth_date(), currentDate).getYears() == age) {
                countPerAge++;
            }

            if (account.getReg_date() != null &&
                    !account.getReg_date().isBefore(firstMonth) &&
                    !account.getReg_date().isAfter(lastMonth)) {
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
        Sort sort = Sort.unsorted();
        Pageable pageable = PageRequest.of(pageDto.getPage(), pageDto.getSize(), sort);
        org.springframework.data.domain.Page<Account> accountsPage = accountRepository.findAll(AccountSpecification.findWithFilter(searchDTO), pageable);

        if (accountsPage == null) {
            throw new IllegalStateException("Page cannot be null");
        }

        List<Account> accounts = accountsPage.getContent();

        log.info("Accounts found: " + accounts.size());
        accounts.forEach(account -> log.info("Account: " + account));

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

        log.info("List accounts received");

        return AccountPageDTO.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .sortDTO(sortDTO)
                .numberOfElements(numberOfElements)
                .pageable(pageableDTO)
                .first(isFirst)
                .last(isLast)
                .size(size)
                .accountMeDTO(mapper.toAccountsMeDtoForAccounts(accounts))
                .number(number)
                .empty(empty)
                .build();
    }
}