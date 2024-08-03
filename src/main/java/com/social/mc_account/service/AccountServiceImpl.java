package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
//import com.social.mc_account.kafka.KafkaConsumer;
//import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
//import com.social.mc_account.dto.KafkaAccountDtoRequest;
import com.social.mc_account.repository.AccountRepository;
import com.social.mc_account.security.JwtUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private  final  AccountMapper mapper;
    private final JwtUtils jwtUtils;
    // private KafkaProducer producer;
    //private KafkaConsumer consumer;


    //МЕТОД РАБОТАЕТ
    @Override
    public AccountDataDTO getDataAccount(String authorization, String email) {
        Account account = accountRepository.findByEmail(email);
        AccountDataDTO accountDataDTO = mapper.toAccountDataDto(account);
        if (account != null) {
            log.info("The account: {} was successfully found by email: {}", account, email);
            return accountDataDTO;
        } else {
            log.warn("The account with email: {} not found", email);
            throw new ResourceNotFoundException("The account with email: " + email + " not found");
        }
    }

    //МЕТОД РАБОТАЕТ
    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Account account = accountRepository.findById(accountMeDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account with ID " + accountMeDTO.getId() + " not found"));
            account = mapper.toAccountMeDto(accountMeDTO);
            account.setLastOnlineTime(LocalDate.now());
            accountRepository.save(account);
            log.info("The account: {} was successfully update", account);
            return accountMeDTO;
    }

    //МЕТОД РАБОТАЕТ
    @Override
    @Transactional
    public AccountMeDTO createAccount(AccountMeDTO accountMeDTO) {
        Account account = new Account();
        account = mapper.toAccountMeDto(accountMeDTO);
        accountRepository.save(account);
        accountMeDTO.setId(account.getId());
        log.info("Account successfully created!");
        return accountMeDTO;
    }


    //Метод по логике работает
    @Override
    public AccountMeDTO getDataMyAccount(String authorization) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Thea account data with id: {} has been successfully received", account.getId());
            return mapper.toAccountMeDtoAccount(account);
        }
        log.warn("The account with id: {} not found", id);
        throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

    //Метод по логике работает
    @Override
    public AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO) {
        UUID id = UUID.fromString(jwtUtils.getId(authorization));
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            accountRepository.save(account);
            log.info("The authorize account: {} successfully updated", account);
            return mapper.toAccountMeDtoAccount(account);
        } else {
            log.warn("The account with id: {} not updated", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not updated");
        }
    }

    //Метод по логике работает
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
                TimeUnit.DAYS.sleep(10); //ЗАМЕНИТЬ НА ЧТО-ТО ИНОЕ
                accountRepository.delete(account);
                log.info("The account with id: {} completely deleted from the database", account.getId());
            } else {
                log.warn("The account with id: {} not found", authorization);
                throw new ResourceNotFoundException("The account with id: " + authorization + " not found");
            }
        }

    @Override
    public String putNotification() {
        UUID userId = UUID.randomUUID(); //НЕРАБОЧИЙ МЕТОД
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Notification sent to friends");
            return "Notification sent to friends of " + account.getFirstName();
        } else {
            log.warn("The account with id: not found");
            throw new ResourceNotFoundException("The account with id: " + " not found");
        }
    }


    //МЕТОД РАБОТАЕТ
    @Override
    public AccountDataDTO getDataById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account with id: {} was successfully found", id);
            return mapper.toAccountDataDto(account);
        }
        log.warn("The account with id: {} not found", id);
        throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

    //МЕТОД РАБОТАЕТ
    @Override
    public void deleteAccountById(UUID id) {
        Optional<Account> user = accountRepository.findById(id);
        if (user.isPresent()) {
            Account thisUser = user.get();
            log.info("The account with id: {} successfully deleted", id);
            accountRepository.delete(thisUser);
        } else {
            log.warn("The account with id: {} not found", id);
            throw new ResourceNotFoundException("The account with id: " + id + " not found");
        }
    }

    @Override
    public List<AccountPageDTO> getAllAccounts(SearchDTO searchDTO, Page page) {
        List<Account> accounts = accountRepository.findAll();
        log.info("All accounts received ");
        return mapper.toPageDtoAccounts(accounts);
    }

    @Override
    public StatisticDTO getStatistic(StatisticRequestDTO statisticRequestDTO) {
        StatisticDTO statistics = new StatisticDTO();
        CountPerAgeDTO countPerAgeDTO = new CountPerAgeDTO();
        CountPerMonthDTO countPerMonthDTO = new CountPerMonthDTO();
        List<Account> allAccount  = accountRepository.findAll();
        LocalDate firstMonth = statisticRequestDTO.getFirstMonth();
        LocalDate lastMonth = statisticRequestDTO.getLastMonth();

        LocalDate birthDate = statisticRequestDTO.getDate();
        LocalDate currentDate = LocalDate.now();
        int age = Period.between(birthDate, currentDate).getYears();
        countPerAgeDTO.setAge(age);

        int countPerAge = countPerAgeDTO.getCount();
        int countPerMonth = countPerMonthDTO.getCount();
        int allCount = countPerAge + countPerMonth;
        for (Account account : allAccount){
            if (account.getBirthDate().equals(statisticRequestDTO.getDate())){
                countPerAge++;
            }
            if ((account.getRegDate().isEqual(firstMonth) || account.getRegDate().isAfter(firstMonth)) &&
                    (account.getRegDate().isEqual(lastMonth) || account.getRegDate().isBefore(lastMonth))) {
               countPerMonth++;
            }
        }
        statistics.setCount(allCount);
        statistics.setDate(statisticRequestDTO.getDate());
        countPerAgeDTO.setAge(age);
        countPerAgeDTO.setCount(countPerAge);
        countPerMonthDTO.setCount(countPerMonth);
        countPerMonthDTO.setDate(statisticRequestDTO.getDate());
        statistics.setCountPerAgeDTO(countPerAgeDTO);
        statistics.setCountPerMonthDTO(countPerMonthDTO);

        log.info("statistic received");
        return statistics;
    }

    @Override
    public List<Account> getListAccounts(SearchDTO searchDTO, Pageable pageable) {
        List<Account> accounts;
        log.info("List accounts received");
       return null;
    }

    @Override
    public List<AccountPageDTO> getAccountsByStatusCode(String statusCode) {
        List<Account> accounts = accountRepository.findByStatusCode(statusCode);
        log.info("All account received by status code: " + statusCode);
        return mapper.toPageDtoAccounts(accounts);
    }
}