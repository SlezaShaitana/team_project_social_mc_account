package com.social.mc_account.mapper;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    @Mapping(source = "first_name", target = "firstName")
    @Mapping(source = "last_name", target = "lastName")
    @Mapping(source = "profile_cover", target = "profileCover")
    @Mapping(source = "status_code", target = "statusCode")
    @Mapping(source = "reg_date", target = "regDate")
    @Mapping(source = "birth_date", target = "birthDate")
    @Mapping(source = "message_permission", target = "messagePermission")
    @Mapping(source = "last_online_time", target = "lastOnlineTime")
    @Mapping(source = "emoji_status", target = "emojiStatus")
    @Mapping(source = "create_on", target = "createdOn")
    @Mapping(source = "update_on", target = "updatedOn")
    @Mapping(source = "deletion_timestamp", target = "deletionTimestamp")
    AccountMeDTO toAccountMeDtoForAccount(Account account);

    @Mapping(source = "firstName", target = "first_name")
    @Mapping(source = "lastName", target = "last_name")
    @Mapping(source = "profileCover", target = "profile_cover")
    @Mapping(source = "statusCode", target = "status_code")
    @Mapping(source = "regDate", target = "reg_date")
    @Mapping(source = "birthDate", target = "birth_date")
    @Mapping(source = "messagePermission", target = "message_permission")
    @Mapping(source = "lastOnlineTime", target = "last_online_time")
    @Mapping(source = "emojiStatus", target = "emoji_status")
    @Mapping(source = "createdOn", target = "create_on")
    @Mapping(source = "updatedOn", target = "update_on")
    @Mapping(source = "deletionTimestamp", target = "deletion_timestamp")
    Account toAccountFromAccountMeDto(AccountMeDTO accountMeDTO);
    List<AccountMeDTO> toAccountsMeDtoForAccounts(List<Account> accounts);
    @Mapping(source = "firstName", target = "first_name")
    @Mapping(source = "lastName", target = "last_name")
    @Mapping(source = "profileCover", target = "profile_cover")
    @Mapping(source = "statusCode", target = "status_code")
    @Mapping(source = "regDate", target = "reg_date")
    @Mapping(source = "birthDate", target = "birth_date")
    @Mapping(source = "messagePermission", target = "message_permission")
    @Mapping(source = "lastOnlineTime", target = "last_online_time")
    @Mapping(source = "emojiStatus", target = "emoji_status")
    @Mapping(source = "createdOn", target = "create_on")
    @Mapping(source = "updatedOn", target = "update_on")
    @Mapping(source = "deletionTimestamp", target = "deletion_timestamp")
    AccountDataDTO toAccountDataDtoFromAccount(Account account);
}