package com.social.mc_account.service;

import com.social.mc_account.dto.*;

import java.util.List;
import java.util.UUID;

public interface AccountService {
    List<AccountDataDTO> getDataAccount(String authorization, String email);

    AccountMeDTO updateAccount(AccountMeDTO accountMeDTO);

    AccountMeDTO createAccount(AccountMeDTO accountMeDTO);

    AccountMeDTO getDataMyAccount(String authorization);

    AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO);

    void deleteAccount(String authorization) throws InterruptedException;

    String putNotification(String authorization, BirthdayDTO birthdayDTO);

    AccountDataDTO getDataById(UUID id);

    void deleteAccountById(UUID id);

    List<AccountPageDTO> getAllAccounts();

    List<StatisticDTO> getStatistic();

    List<AccountPageDTO> getListAccounts(AccountPageDTO accountPageDTO);

    List<AccountPageDTO> getAccountsByStatusCode(String statusCode);
}
