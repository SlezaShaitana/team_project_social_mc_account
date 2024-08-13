package com.social.mc_account.mapper;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccountFromAccountMeDto(AccountMeDTO accountMeDTO);
    AccountMeDTO toAccountMeDtoForAccount(Account account);
    List<AccountMeDTO> toAccountsMeDtoForAccounts(List<Account> accounts);
    AccountDataDTO toAccountDataDtoFromAccount(Account account);
    List<AccountPageDTO> toPageDtoAccountsFromAccounts(List<Account> accounts);
}