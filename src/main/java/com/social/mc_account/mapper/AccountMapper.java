package com.social.mc_account.mapper;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    AccountMeDTO toAccountMeDtoForAccount(Account account);
    Account toAccountFromAccountMeDto(AccountMeDTO accountMeDTO);
    List<AccountMeDTO> toAccountsMeDtoForAccounts(List<Account> accounts);
    AccountDataDTO toAccountDataDtoFromAccount(Account account);
}