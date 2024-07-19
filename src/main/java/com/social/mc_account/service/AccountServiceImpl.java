package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import com.social.mc_account.repository.AccountRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@EnableAsync
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public List<AccountDataDTO> getDataAccount(String authorization, String email) {
        return null;
    }

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        return null;
    }

    @Override
    @Transactional
    public AccountMeDTO createAccount(AccountMeDTO accountMeDTO) {
        Account account = new Account();
        if (accountMeDTO.getId() == null) {
            account.setId(UUID.randomUUID());
        } else {
            account.setId(accountMeDTO.getId());
        }
        account.setFirstName(accountMeDTO.getFirstName());
        account.setLastName(accountMeDTO.getLastName());
        account.setEmail(accountMeDTO.getEmail());
        account.setPassword(accountMeDTO.getPassword());
        account.setPhone(accountMeDTO.getPhone());
        account.setPhoto(accountMeDTO.getPhoto());
        account.setProfileCover(accountMeDTO.getProfileCover());
        account.setAbout(accountMeDTO.getAbout());
        account.setCity(accountMeDTO.getCity());
        account.setCountry(accountMeDTO.getCountry());
        account.setStatusCode(accountMeDTO.getStatusCode());
        account.setRegDate(new Date());
        account.setBirthDate(accountMeDTO.getBirthDate());
        account.setMessagePermission(accountMeDTO.getMessagePermission());
        account.setLastOnlineTime(new Date());
        account.setOnline(false);
        account.setBlocked(false);
        account.setEmojiStatus(accountMeDTO.getEmojiStatus());
        account.setCreatedOn(new Date());
        account.setUpdateOn(new Date());
        account.setDeletionTimestamp(null);
        account.setDeleted(false);

        account = accountRepository.save(account);

        accountMeDTO.setId(account.getId());
        return accountMeDTO;
    }

    @Override
    public AccountMeDTO getDataMyAccount(String authorization) {
        return null;
    }

    @Override
    public AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO) {
        return null;
    }

    @Override
    @Async
    public void deleteAccount(String authorization) throws InterruptedException {
        UUID userId = extractUserIdFromAuthorization(authorization);

        Optional<Account> optionalUser = accountRepository.findById(userId);

        if(optionalUser.isPresent()){
            Account account = optionalUser.get();
            account.setDeleted(true);
            accountRepository.save(account);

            TimeUnit.DAYS.sleep(10);

            accountRepository.delete(account);
        } else {
            throw new IllegalArgumentException("Account not found");
        }
    }

    @Override
    public String putNotification(String authorization, BirthdayDTO birthdayDTO) {
        return null;
    }

    @Override
    public AccountDataDTO getDataById(UUID id) {
        Account account = new Account();
        accountRepository.findById(id);

        return null;
    }

    @Override
    public void deleteAccountById(UUID id) {
        Optional<Account> user = accountRepository.findById(id);

        if(user.isPresent()){
            Account thisUser = user.get();
            accountRepository.delete(thisUser);
        } else {
            throw new IllegalArgumentException("Account not found with id: " + id);
        }
    }

    @Override
    public List<AccountPageDTO> getAllAccounts() {
        return null;
    }

    @Override
    public List<StatisticDTO> getStatistic() {
        return null;
    }

    @Override
    public List<AccountPageDTO> getListAccounts(AccountPageDTO accountPageDTO) {
        return null;
    }

    @Override
    public List<AccountPageDTO> getAccountsByStatusCode(String statusCode) {
    return null;
    }

    private UUID extractUserIdFromAuthorization(String authorization) {
        return UUID.fromString(authorization);
    }
}
