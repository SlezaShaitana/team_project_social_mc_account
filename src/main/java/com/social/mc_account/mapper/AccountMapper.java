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
        //Или здесь тоже надо через ObjectMapper???
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
        //И тут?
        Account.builder()
                .id(accountMeDTO.getId())
                .firstName(accountMeDTO.getFirstName())
                .lastName(accountMeDTO.getLastName())
                .email(accountMeDTO.getEmail())
                .password(accountMeDTO.getPassword())
                .phone(accountMeDTO.getPhone())
                .photo(accountMeDTO.getPhoto())
                .profileCover(accountMeDTO.getProfileCover())
                .about(accountMeDTO.getAbout())
                .city(accountMeDTO.getCity())
                .country(accountMeDTO.getCountry())
                .statusCode(accountMeDTO.getStatusCode())
                .regDate(new Date())
                .birthDate(accountMeDTO.getBirthDate())
                .messagePermission(account.getMessagePermission())
                .lastOnlineTime(new Date())
                .isOnline(false)
                .isBlocked(false)
                .emojiStatus(accountMeDTO.getEmojiStatus())
                .updatedOn(LocalDateTime.now())
                .deletionTimestamp(null)
                .isDeleted(false).build();
    }
}
