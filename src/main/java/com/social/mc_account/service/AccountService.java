package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    Account getDataAccount(String authorization, String email);

    AccountMeDTO updateAccount(AccountMeDTO accountMeDTO);

    AccountMeDTO createAccount(AccountMeDTO accountMeDTO);

    AccountMeDTO getDataMyAccount(String authorization);

    AccountMeDTO updateAuthorizeAccount(String authorization);

    void deleteAccount(String authorization) throws InterruptedException;

    String putNotification();

    AccountDataDTO getDataById(UUID id);

    void deleteAccountById(UUID id);

    List<AccountPageDTO> getAllAccounts();

    List<StatisticDTO> getStatistic();

    List<Account> getListAccounts(Account account);

    List<AccountPageDTO> getAccountsByStatusCode(String statusCode);
}
