package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
import com.social.mc_account.kafka.KafkaConsumer;
import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
import com.social.mc_account.model.KafkaAccountDtoRequest;
import com.social.mc_account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class AccountServiceImpl implements AccountService {
    private AccountRepository accountRepository;

    private KafkaProducer producer;
    private KafkaConsumer consumer;
    AccountMapper mapper = new AccountMapper();

    @Override
    public Account getDataAccount(String authorization, String email) {
        Account account = accountRepository.findByEmail(email);
        if (account != null) {
            log.info("The account: {} was successfully found by email: {}", account, email);
            return account;
        } else {
            log.warn("The account with email: {} not found", email);
            throw new ResourceNotFoundException("The account with email: " + email + " not found");
        }
    }

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Account account = accountRepository.findById(accountMeDTO.getId()).orElseThrow();
            HashMap<String, Object> updateAccount = new HashMap<>();
            mapper.updateAccountFromDTO(account, accountMeDTO);
            accountRepository.save(account);
            updateAccount.put("account", account);
            producer.sendMessage(updateAccount);
            log.info("The account: {} was successfully update", account);
            return accountMeDTO;
    }

    @Override
    @Transactional
    public AccountMeDTO createAccount(AccountMeDTO accountMeDTO) {
        KafkaAccountDtoRequest kafkaAccount = new KafkaAccountDtoRequest();
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
            log.info("Thea account data with id: {} has been successfully received", account.getId());
            return mapper.convertToAccountMeDTO(account);
        }
        log.warn("The account with id: {} not found", authorization);
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
            log.info("The authorize account: {} successfully updated", account);
            return mapper.convertToAccountMeDTO(account);
        } else {
            log.warn("The account with id: {} not updated", authorization);
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
            log.info("The account with id: {} successfully softly deleted", account.getId());
            TimeUnit.DAYS.sleep(10);
            accountRepository.delete(account);
            log.info("The account with id: {} completely deleted from the database", account.getId());
        } else {
            log.warn("The account with id: {} not found", authorization);
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
            log.warn("The account with id: {} not found", authorization);
            throw new ResourceNotFoundException("The account with id: " + authorization + " not found");
        }
    }

    @Override
    public AccountDataDTO getDataById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("The account with id: {} was successfully found", id);
            return mapper.convertToAccountDataDTO(account);
        }
        log.warn("The account with id: {} not found", id);
        throw new ResourceNotFoundException("The account with id: " + id + " not found");
    }

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