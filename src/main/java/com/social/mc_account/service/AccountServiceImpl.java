package com.social.mc_account.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
import com.social.mc_account.kafka.KafkaConsumer;
import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
import com.social.mc_account.model.KafkaAccount;
import com.social.mc_account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@EnableAsync
@Slf4j
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    private KafkaProducer producer;
    private KafkaConsumer consumer;
    AccountMapper mapper = new AccountMapper();

    @Override
    public Account getDataAccount(String authorization, String email) {
        Account account = accountRepository.findByEmail(email);
        if(account != null){
            log.info("The account: " + account + "was successfully found by email: " + email);
            return account;
        } else {
            throw new ResourceNotFoundException("The account with email: " + email + " not found");
        }
    }

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Optional<Account> optionalAccount = accountRepository.findById(accountMeDTO.getId());
        if (optionalAccount.isPresent()) {
            HashMap<String, Object> updateAccount = new HashMap<>();
            Account account = optionalAccount.get();
            mapper.updateAccountFromDTO(account, accountMeDTO);
            accountRepository.save(account);
            updateAccount.put("account", account);
            producer.sendMessage(updateAccount);
            log.info("The account: " + account + "was successfully update");
            return accountMeDTO;
        } else {
            throw new ResourceNotFoundException("The account with id: " + accountMeDTO.getId() + " not found");
        }
    }

    @Override
    @Transactional
    public AccountMeDTO createAccount(AccountMeDTO accountMeDTO) {
        KafkaAccount kafkaAccount = new KafkaAccount();
        HashMap<String, Object> kafkaMessage = new HashMap<>();
        kafkaMessage.put("Account", kafkaAccount);
        Account account = new Account();
        if (accountMeDTO.getId() == null) {
            account.setId((UUID.randomUUID()));
        } else {
            account.setId(accountMeDTO.getId());
        }
        mapper.updateAccountFromDTO(account, accountMeDTO);
        accountRepository.save(account);
        accountMeDTO.setId(account.getId());
        log.info("Account successfully created!");
        return accountMeDTO;
    }

    @Override
    public AccountMeDTO getDataMyAccount(String authorization) {
        UUID userId = extractUserIdFromAuthorization(authorization);
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Thea account data with id: " + account.getId() + " has been successfully received");
            return mapper.convertToAccountMeDTO(account);
        }
        throw new ResourceNotFoundException("The account with id: " + authorization + " not found");
    }

    @Override
    public AccountMeDTO updateAuthorizeAccount(String authorization) {
        UUID userId = extractUserIdFromAuthorization(authorization);
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setLastOnlineTime(new Date());
            account.setOnline(true);
            accountRepository.save(account);
            log.info("The authorize account: " + account + " successfully updated");
            return mapper.convertToAccountMeDTO(account);
        } else {
            throw new ResourceNotFoundException("The account with id: " + authorization + " not updated");
        }
    }

    @Override
    @Async
    public void deleteAccount(String authorization) throws InterruptedException {
        UUID userId = extractUserIdFromAuthorization(authorization);
        Optional<Account> optionalUser = accountRepository.findById(userId);
        if (optionalUser.isPresent()) {
            Account account = optionalUser.get();
            account.setDeleted(true);
            accountRepository.save(account);
            log.info("The account with id: " + account.getId() + " successfully softly deleted");
            TimeUnit.DAYS.sleep(10);
            accountRepository.delete(account);
            log.info("The account with id: " + account.getId() + " completely deleted from the database");
        } else {
            throw new ResourceNotFoundException("The account with id: " + authorization + " not found");
        }
    }

    @Override
    public String putNotification() {
        String authorization = getAuthorizationFromContext();
        UUID userId = extractUserIdFromAuthorization(authorization);
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Notification sent to friends");
            return "Notification sent to friends of " + account.getFirstName();
        } else {
            throw new ResourceNotFoundException("The account with id: " + authorization + " not found");
        }
    }

    @Override
    public AccountDataDTO getDataById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account with id: " + id  + " was successfully found");
            return mapper.convertToAccountDataDTO(account);
        }
        throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

    @Override
    public void deleteAccountById(UUID id) {
        Optional<Account> user = accountRepository.findById(id);

        if (user.isPresent()) {
            Account thisUser = user.get();
            log.info("The account with id: " + id + "successfully deleted");
            accountRepository.delete(thisUser);
        } else {
            throw new ResourceNotFoundException("The account with id: " + id + " not found");
        }
    }

    @Override
    public List<AccountPageDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        log.info("All accounts received ");
        return accounts.stream().map(mapper::convertToAccountPageDTO).collect(Collectors.toList());
    }

    @Override
    public List<StatisticDTO> getStatistic() {
        List<StatisticDTO> statistics = new ArrayList<>();
        log.info("statistic received");
        return statistics;
    }

    @Override
    public List<Account> getListAccounts(Account account) {
        List<Account> accounts = accountRepository.findAccountsById(account.getId());
        log.info("List accounts received");
        return accounts;
    }

    @Override
    public List<AccountPageDTO> getAccountsByStatusCode(String statusCode) {
        List<Account> accounts = accountRepository.findByStatusCode(statusCode);
        log.info("All account received by status code: " + statusCode);
        return accounts.stream().map(mapper::convertToAccountPageDTO).collect(Collectors.toList());
    }


    private UUID extractUserIdFromAuthorization(String authorization) {
        return UUID.fromString(authorization);
    }

    private String getAuthorizationFromContext() {

     return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
    }
}