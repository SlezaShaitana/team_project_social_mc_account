package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.exception.ResourceNotFoundException;
//import com.social.mc_account.kafka.KafkaConsumer;
//import com.social.mc_account.kafka.KafkaProducer;
import com.social.mc_account.mapper.AccountMapper;
import com.social.mc_account.model.Account;
//import com.social.mc_account.model.KafkaAccountDtoRequest;
import com.social.mc_account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableAsync
@Slf4j
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;
    private  final  AccountMapper mapper;
    // private KafkaProducer producer;
    //private KafkaConsumer consumer;

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

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Account account = accountRepository.findById(accountMeDTO.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Account with ID " + accountMeDTO.getId() + " not found"));
            account = mapper.toAccountMeDto(accountMeDTO);
            accountRepository.save(account);
            log.info("The account: {} was successfully update", account);
            return accountMeDTO;
    }

    @Override
    @Transactional
    public AccountMeDTO createAccount(AccountMeDTO accountMeDTO) {
       // KafkaAccountDtoRequest kafkaAccount = new KafkaAccountDtoRequest();
       // HashMap<String, Object> kafkaMessage = new HashMap<>();
        //kafkaMessage.put("Account", kafkaAccount);
        Account account = new Account();
        if (accountMeDTO.getId() == null) {
            account.setId((UUID.randomUUID()));
        } else {
            account.setId(accountMeDTO.getId());
        }
        account = mapper.toAccountMeDto(accountMeDTO);
        accountRepository.save(account);
        accountMeDTO.setId(account.getId());
        log.info("Account successfully created!");
        return accountMeDTO;
    }

    @Override
    public AccountMeDTO getDataMyAccount(@AuthenticationPrincipal UserDetails userDetails) {
        UUID userId = UUID.randomUUID(); //НЕРАБОЧИЙ МЕТОД
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Thea account data with id: {} has been successfully received", account.getId());
            return mapper.toAccountMeDtoAccount(account);
        }
        log.warn("The account with username: {} not found", userDetails.getUsername());
        throw new ResourceNotFoundException("The account with username: " + userDetails.getUsername() + " not found");
    }

    @Override
    public Account updateAuthorizeAccount(String authorization) {
        UUID userId = UUID.randomUUID(); //НЕРАБОЧИЙ МЕТОД
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            account.setLastOnlineTime(new Date());
            account.setOnline(true);
            accountRepository.save(account);
            log.info("The authorize account: {} successfully updated", account);
            return account;
        } else {
            log.warn("The account with id: {} not updated", authorization);
            throw new ResourceNotFoundException("The account with id: " + authorization + " not updated");
        }
    }

        @Override
        @Async
        public void deleteAccount(String authorization) throws InterruptedException {
            UUID userId = UUID.randomUUID(); //НЕРАБОЧИЙ МЕТОД
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
        UUID userId = UUID.randomUUID(); //НЕРАБОЧИЙ МЕТОД
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            log.info("Notification sent to friends");
            return "Notification sent to friends of " + account.getFirstName();
        } else {
            // log.warn("The account with id: {} not found", authorization);
            throw new ResourceNotFoundException("The account with id: " + " not found");
        }
    }

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
        return mapper.toPageDtoAccounts(accounts);
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
        return mapper.toPageDtoAccounts(accounts);
    }

    @Override
    public Account findUserByUsername(String username) {
        return accountRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }
}