package com.social.mc_account.controller;

import com.social.mc_account.dto.*;
import com.social.mc_account.service.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.*;;

@RestController
@CrossOrigin(origins = "http://79.174.80.200")
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
public class ApiController {
    private final AccountServiceImpl accountService;

    @GetMapping
    public AccountDataDTO getDataAccountByEmail(@RequestHeader String authorization, @RequestParam String email) {
        return accountService.getDataAccount(authorization, email);
    }

    @PutMapping
    public AccountMeDTO updateDataAccount(@RequestBody AccountMeDTO accountMeDTO) {
        return accountService.updateAccount(accountMeDTO);
    }

    @PostMapping
    public AccountMeDTO createAccount(@RequestBody AccountDtoRequest accountDtoRequest) {
        return accountService.createAccount(accountDtoRequest);
    }

    @GetMapping("/me")
    public AccountMeDTO getDataMyAccount(@RequestHeader("Authorization") String authorization) {
         return accountService.getDataMyAccount(authorization);
    }

    @PutMapping("/me")
    public AccountMeDTO updateDataMyAccount(@RequestHeader String authorization, @RequestBody AccountMeDTO accountMeDTO) {
        return accountService.updateAuthorizeAccount(authorization, accountMeDTO);
    }

    @DeleteMapping("/me")
    public void deleteMyAccount(@RequestHeader String authorization) throws InterruptedException {
        accountService.deleteAccount(authorization);
    }

    @PutMapping("/birthdays")
    public void putNotificationsForFriends() {
        accountService.putNotification();
    }

    @GetMapping("/{id}")
    public AccountDataDTO getDataMyAccountById(@PathVariable UUID id) {
        return accountService.getDataById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteMyAccountById(@PathVariable UUID id) {
        accountService.deleteAccountById(id);
    }

    @GetMapping("/statistic")
    public StatisticDTO getStatisticAccounts(@ModelAttribute StatisticRequestDTO requestDTO) {
        return accountService.getStatistic(requestDTO);
    }

    @GetMapping("/search")
    public AccountPageDTO getListAccounts(@ModelAttribute SearchDTO searchDTO, @ModelAttribute Page pageable) {
        return accountService.getListAccounts(searchDTO, pageable);
    }

    @GetMapping("/search/statusCode")
    public AccountPageDTO getListAccountsByStatus(@ModelAttribute SearchDTO searchDTO, @ModelAttribute Page pageable) {
        return accountService.getListAccounts(searchDTO, pageable);
    }
}