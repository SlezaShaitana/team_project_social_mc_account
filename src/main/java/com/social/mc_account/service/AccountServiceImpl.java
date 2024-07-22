package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import com.social.mc_account.repository.AccountRepository;
import jakarta.transaction.Transactional;
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
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public Account getDataAccount(String authorization, String email) {
        Account account = accountRepository.findByEmail(email);
        if(account != null){
            return account;
        }
        return null;
    }

    @Override
    public AccountMeDTO updateAccount(AccountMeDTO accountMeDTO) {
        Optional<Account> optionalAccount = accountRepository.findById(accountMeDTO.getId());
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            updateAccountFromDTO(account, accountMeDTO);
            accountRepository.save(account);
            return accountMeDTO;
        } else {
            return null;
        }
    }

    @Override
    @Transactional
    public AccountMeDTO createAccount(AccountMeDTO accountMeDTO) {
        Account account = new Account();
        if (accountMeDTO.getId() == null) {
            account.setId((UUID.randomUUID()));
        } else {
            account.setId(accountMeDTO.getId());
        }
        updateAccountFromDTO(account, accountMeDTO);
        accountRepository.save(account);
        accountMeDTO.setId(account.getId());
        return accountMeDTO;
    }

    @Override
    public AccountMeDTO getDataMyAccount(String authorization) {
        UUID userId = extractUserIdFromAuthorization(authorization);
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return convertToAccountMeDTO(account);
        }
        return null;
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
            return convertToAccountMeDTO(account);
        } else {
            return null;
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

            TimeUnit.DAYS.sleep(10);

            accountRepository.delete(account);
        } else {
            throw new IllegalArgumentException("Account not found");
        }
    }

    @Override
    public String putNotification() {
        String authorization = getAuthorizationFromContext();
        UUID userId = extractUserIdFromAuthorization(authorization);
        Optional<Account> optionalAccount = accountRepository.findById(userId);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return "Notification sent to friends of " + account.getFirstName();
        }
        return "Account not found";
    }

    @Override
    public AccountDataDTO getDataById(UUID id) {
        Optional<Account> optionalAccount = accountRepository.findById(id);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            return convertToAccountDataDTO(account);
        }
        return null;
    }

    @Override
    public void deleteAccountById(UUID id) {
        Optional<Account> user = accountRepository.findById(id);

        if (user.isPresent()) {
            Account thisUser = user.get();
            accountRepository.delete(thisUser);
        } else {
            throw new IllegalArgumentException("Account not found with id: " + id);
        }
    }

    @Override
    public List<AccountPageDTO> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream().map(this::convertToAccountPageDTO).collect(Collectors.toList());
    }

    @Override
    public List<StatisticDTO> getStatistic() {
        List<StatisticDTO> statistics = new ArrayList<>();
        return statistics;
    }

    @Override
    public List<Account> getListAccounts(Account account) {
        List<Account> accounts = accountRepository.findAccountsById(account.getId());
        return accounts;
    }

    @Override
    public List<AccountPageDTO> getAccountsByStatusCode(String statusCode) {
        List<Account> accounts = accountRepository.findByStatusCode(statusCode);
        return accounts.stream().map(this::convertToAccountPageDTO).collect(Collectors.toList());
    }


    private UUID extractUserIdFromAuthorization(String authorization) {
        return UUID.fromString(authorization);
    }

    private AccountDataDTO convertToAccountDataDTO(Account account) {
        AccountDataDTO dto = new AccountDataDTO();
        dto.setId(account.getId());
        dto.setEmail(account.getEmail());
        dto.setPassword(account.getPassword());
        dto.setRoles(account.getRole());
        dto.setDeleted(account.isDeleted());
        dto.setFirstName(account.getFirstName());
        return dto;
    }

    private AccountMeDTO convertToAccountMeDTO(Account account) {
        AccountMeDTO dto = new AccountMeDTO();
        dto.setId(account.getId());
        dto.setFirstName(account.getFirstName());
        dto.setLastName(account.getLastName());
        dto.setEmail(account.getEmail());
        dto.setPassword(account.getPassword());
        dto.setPhone(account.getPhone());
        dto.setPhoto(account.getPhoto());
        dto.setProfileCover(account.getProfileCover());
        dto.setAbout(account.getAbout());
        dto.setCity(account.getCity());
        dto.setCountry(account.getCountry());
        dto.setStatusCode(account.getStatusCode());
        dto.setRegDate(account.getRegDate());
        dto.setBirthDate(account.getBirthDate());
        dto.setMessagePermission(account.getMessagePermission());
        dto.setLastOnlineTime(account.getLastOnlineTime());
        dto.setOnline(account.isOnline());
        dto.setBlocked(account.isBlocked());
        dto.setEmojiStatus(account.getEmojiStatus());
        dto.setCreatedOn(account.getCreatedOn());
        dto.setUpdatedOn(account.getUpdatedOn());
        dto.setDeletionTimestamp(account.getDeletionTimestamp());
        dto.setDeleted(account.isDeleted());
        return dto;
    }

    private AccountPageDTO convertToAccountPageDTO(Account account) {
        AccountPageDTO dto = new AccountPageDTO();
        dto.setAccountMeDTO(convertToAccountMeDTO(account));
        dto.setTotalElements(0);
        dto.setTotalPages(0);
        dto.setNumberOfElements(0);
        dto.setSortDTO(new SortDTO());
        dto.setPageable(new PageableDTO());
        dto.setFirst(false);
        dto.setLast(false);
        dto.setSize(0);
        dto.setNumber(0);
        dto.setEmpty(false);
        return dto;
    }

    private void updateAccountFromDTO(Account account, AccountMeDTO accountMeDTO) {
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
        account.setUpdatedOn(LocalDateTime.now());
        account.setDeletionTimestamp(null);
        account.setDeleted(false);
    }

    private String getAuthorizationFromContext() {

     return SecurityContextHolder.getContext().getAuthentication().getCredentials().toString();
    }
}