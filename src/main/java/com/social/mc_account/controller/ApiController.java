package com.social.mc_account.controller;

import com.social.mc_account.dto.*;
import com.social.mc_account.service.AccountServiceImpl;
import com.social.mc_account.utils.UrlParseUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

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
    public AccountMeDTO createAccount(@RequestBody RegistrationDto accountDtoRequest) {
        return accountService.createAccount(accountDtoRequest);
    }

    @GetMapping("/me")
    public AccountMeDTO getDataMyAccount(@RequestHeader("Authorization") String authorization) {
         return accountService.getDataMyAccount(authorization);
    }

    @PutMapping("/me")
    public AccountMeDTO updateDataMyAccount(@RequestHeader String authorization,
            @RequestBody AccountMeDTO accountMeDTO) {
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
    public AccountMeDTO getDataMyAccountById(@PathVariable UUID id) {
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
    public AccountPageDTO getListAccounts(@RequestParam(required = false) List<UUID> ids,
                                          @RequestParam(required = false) Boolean isDeleted,
                                          @Valid @RequestParam(required = false) Page pageable,
                                          HttpServletRequest request
    ) {
        String url = request.getQueryString();
        Page page = UrlParseUtils.getPageable(url);

        SearchDTO searchDTO = UrlParseUtils.getSearchDTO(url);
        searchDTO.setIds(ids);
        return accountService.getListAccounts(searchDTO, page);
    }

    @GetMapping("/search/statusCode")
    public AccountPageDTO getListAccountsByStatus(@ModelAttribute SearchDTO searchDTO, @ModelAttribute Page pageable) {
        return accountService.getListAccounts(searchDTO, pageable);
    }
}