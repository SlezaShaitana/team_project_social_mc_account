package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import java.util.*;

public interface AccountService {
    AccountDataDTO getDataAccount(String authorization, String email);
    AccountMeDTO updateAccount(AccountMeDTO accountMeDTO);
    AccountMeDTO createAccount(AccountDtoRequest accountDtoRequest);
    AccountMeDTO getDataMyAccount(String authorization);
    AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO);
    void deleteAccount(String authorization) throws InterruptedException;
    void putNotification();
    AccountDataDTO getDataById(UUID id);
    void deleteAccountById(UUID id);
    List<AccountPageDTO> getAllAccounts(SearchDTO searchDTO, Page page);
    StatisticDTO getStatistic(StatisticRequestDTO statisticRequestDTO);
    AccountPageDTO getListAccounts(SearchDTO searchDTO, Page pageDto);
    AccountPageDTO getAccountsByStatusCode(SearchDTO searchDTO, Page page);
}