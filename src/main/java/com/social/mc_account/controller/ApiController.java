package com.social.mc_account.controller;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import com.social.mc_account.service.AccountServiceImpl;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Data
public class ApiController {

    @Autowired
    private AccountServiceImpl accountService;

    @GetMapping("/account")
    public Account getDataAccountByEmail(@RequestHeader String authorization, String email){
        return accountService.getDataAccount(authorization, email);
    }

    @PutMapping("/account")
    public AccountMeDTO updateDataAccount(@RequestBody AccountMeDTO accountMeDTO){
        return accountService.updateAccount(accountMeDTO);
    }

    @PostMapping("/account")
    public AccountMeDTO createAccount(@RequestBody AccountMeDTO accountMeDTO){
        return accountService.createAccount(accountMeDTO);
    }

    @GetMapping("/account/me")
    public AccountMeDTO getDataMyAccount(@RequestHeader String authorization){
        return  accountService.getDataMyAccount(authorization);
    }

    @PutMapping("/account/me")
    public AccountMeDTO updateDataMyAccount(@RequestHeader String authorization){
        return accountService.updateAuthorizeAccount(authorization);
    }

    @DeleteMapping("/account/me")
    public void deleteMyAccount(@RequestHeader String authorization) throws InterruptedException {
         accountService.deleteAccount(authorization);
    }

    @PutMapping("/account/birthdays")
    public String putNotificationsForFriends(){
        return accountService.putNotification();
    }

    @GetMapping("/account/{id}")
    public AccountDataDTO getDataMyAccountById(@PathVariable UUID id){
        return accountService.getDataById(id);
    }

    @DeleteMapping("/account/{id}")
    public void deleteMyAccountById(@PathVariable UUID id){
        accountService.deleteAccountById(id);
    }

    @GetMapping("/account/unsupported")
    public List<AccountPageDTO> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/account/statistic")
    public List<StatisticDTO> getStatisticAccounts(StatisticRequestDTO requestDTO){
        return accountService.getStatistic();
    }

    @GetMapping("/account/search")
    public List<Account> getListAccounts(Account account){
        return accountService.getListAccounts(account);
    }

    @GetMapping("/account/search/statusCode")
    public List<AccountPageDTO> getListAccountsByStatus(@PathVariable String statusCode){
        return accountService.getAccountsByStatusCode(statusCode);
    }
}