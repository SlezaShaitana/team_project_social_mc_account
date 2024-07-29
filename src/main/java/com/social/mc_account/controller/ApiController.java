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
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class ApiController {

    private final AccountServiceImpl accountService;

    @GetMapping
    public Account getDataAccountByEmail(@RequestHeader String authorization, @RequestParam String email){
        return accountService.getDataAccount(authorization, email);
    }

    @PutMapping
    public AccountMeDTO updateDataAccount(@RequestBody AccountMeDTO accountMeDTO){
        return accountService.updateAccount(accountMeDTO);
    }

    @PostMapping
    public AccountMeDTO createAccount(@RequestBody AccountMeDTO accountMeDTO){
        return accountService.createAccount(accountMeDTO);
    }

    @GetMapping("/me")
    public AccountMeDTO getDataMyAccount(@RequestHeader String authorization){
        return  accountService.getDataMyAccount(authorization);
    }

    @PutMapping("/me")
    public AccountMeDTO updateDataMyAccount(@RequestHeader String authorization){
        return accountService.updateAuthorizeAccount(authorization);
    }

    @DeleteMapping("/me")
    public void deleteMyAccount(@RequestHeader String authorization) throws InterruptedException {
         accountService.deleteAccount(authorization);
    }

    @PutMapping("/birthdays")
    public String putNotificationsForFriends(){
        return accountService.putNotification();
    }

    @GetMapping("/{id}")
    public AccountDataDTO getDataMyAccountById(@PathVariable UUID id){
        return accountService.getDataById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteMyAccountById(@PathVariable UUID id){
        accountService.deleteAccountById(id);
    }

    @GetMapping("/unsupported")
    public List<AccountPageDTO> getAllAccounts(){
        return accountService.getAllAccounts();
    }

    @GetMapping("/statistic")
    public List<StatisticDTO> getStatisticAccounts(@RequestParam StatisticRequestDTO requestDTO){
        return accountService.getStatistic();
    }

    @GetMapping("/search")
    public List<Account> getListAccounts(@RequestParam Account account){
        return accountService.getListAccounts(account);
    }

    @GetMapping("/search/statusCode")
    public List<AccountPageDTO> getListAccountsByStatus(@PathVariable String statusCode){
        return accountService.getAccountsByStatusCode(statusCode);
    }
}