package com.social.mc_account.service;

import com.social.mc_account.dto.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface AccountService {
    AccountDataDTO getDataAccount(String authorization, String email);
    AccountMeDTO updateAccount(AccountMeDTO accountMeDTO);
    AccountMeDTO createAccount(RegistrationDto accountDtoRequest);
    AccountMeDTO getDataMyAccount(String authorization);
    AccountMeDTO updateAuthorizeAccount(String authorization, AccountMeDTO accountMeDTO);
    void deleteAccount(String authorization) throws InterruptedException;
    void putNotification();
    AccountMeDTO getDataById(UUID id);
    void deleteAccountById(UUID id);
    StatisticDTO getStatistic(StatisticRequestDTO statisticRequestDTO);
    AccountPageDTO getListAccounts(SearchDTO searchDTO, Page pageDto);
    List<UUID> getListIdsByFirstNameAndLastName(SearchDTO searchDTO);
}