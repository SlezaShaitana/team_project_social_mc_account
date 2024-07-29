package com.social.mc_account.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class AccountMapper {
    public AccountDataDTO convertToAccountDataDTO(Account account) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(account, AccountDataDTO.class);
    }

    public AccountMeDTO convertToAccountMeDTO(Account account) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(account, AccountMeDTO.class);
    }

    public AccountPageDTO convertToAccountPageDTO(Account account) {
        return AccountPageDTO.builder()
                .accountMeDTO(convertToAccountMeDTO(account))
                .totalElements(0)
                .totalPages(0)
                .numberOfElements(0)
                .sortDTO(new SortDTO())
                .pageable(new PageableDTO())
                .first(false)
                .last(false)
                .size(0)
                .number(0)
                .empty(false)
                .build();
    }

    public void updateAccountFromDTO(Account account, AccountMeDTO accountMeDTO) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.convertValue(account, AccountMeDTO.class);
    }
}
