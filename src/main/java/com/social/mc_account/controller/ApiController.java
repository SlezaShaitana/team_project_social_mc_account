package com.social.mc_account.controller;

import com.social.mc_account.dto.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Data
public class ApiController {

    @GetMapping("/account")
    public ResponseEntity<AccountDataDTO> getDataAccountByEmail(String authorization, String email){
        return null;
    }

    @PutMapping("/account")
    public ResponseEntity<AccountMeDTO> updateDataAccount(){
        return null;
    }

    @PostMapping("/account")
    public ResponseEntity<AccountMeDTO> createAccount(){
        return null;
    }

    @GetMapping("/account/me")
    public ResponseEntity<AccountMeDTO> getDataMyAccount(String authorization){
        return null;
    }

    @PutMapping("/account/me")
    public ResponseEntity<AccountMeDTO> updateDataMyAccount(String authorization){
        return null;
    }

    @DeleteMapping("/account/me")
    public Response deleteMyAccount(String authorization){
        return null;
    }

    @PutMapping("/account/birthdays")
    public ResponseEntity<BirthdayDTO> putNotificationsForFriends(){
        return null;
    }

    @GetMapping("/account/{id}")
    public ResponseEntity<AccountMeDTO> getDataMyAccountById(String id){
        return null;
    }

    @DeleteMapping("/account/{id}")
    public Response deleteMyAccountById(String id){
        return null;
    }

    @GetMapping("/account/unsupported")
    public ResponseEntity<AccountPageDTO> getAllAccounts(SearchDTO searchDTO, Page page){
        return null;
    }

    @GetMapping("/account/statistic")
    public ResponseEntity<StatisticDTO> getStatisticAccounts(StatisticRequestDTO requestDTO){
        return null;
    }

    @GetMapping("/account/search")
    public ResponseEntity<AccountPageDTO> getListAccounts(SearchDTO searchDTO, PageableDTO pageableDTO){
        return null;
    }

    @GetMapping("/account/search/statusCode")
    public ResponseEntity<AccountPageDTO> getListAccountsByStatus(SearchDTO searchDTO, PageableDTO pageableDTO){
        return null;
    }
}