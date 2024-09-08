package com.social.mc_account.controller;

import com.social.mc_account.dto.*;
import com.social.mc_account.service.AccountServiceImpl;
import com.social.mc_account.utils.UrlParseUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
    public AccountMeDTO updateAccount(
            @RequestHeader("Authorization") String authorization,
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
    public AccountPageDTO getListAccounts(
            @RequestParam(required = false) List<UUID> ids,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request
    ) {
        String url = request.getQueryString();
        Page pageDto = new Page(page, size, null);

        SearchDTO searchDTO = UrlParseUtils.getSearchDTO(url);
        searchDTO.setIds(ids);

        return accountService.getListAccounts(searchDTO, pageDto);
    }


    @GetMapping("/search/statusCode")
    public AccountPageDTO getListAccountsByStatus(@ModelAttribute SearchDTO searchDTO, @ModelAttribute Page pageable) {
        return accountService.getListAccounts(searchDTO, pageable);
    }

    @GetMapping("/search_by_fullName")
    public List<UUID> getListIdsByFirstNameAndLastName(@RequestParam String firstName,
                                                       @RequestParam String lastName) {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setFirstName(firstName);
        searchDTO.setLastName(lastName);
        return accountService.getListIdsByFirstNameAndLastName(searchDTO);
    }
}