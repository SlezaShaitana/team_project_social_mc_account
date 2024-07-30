package com.social.mc_account.mapper;

import com.social.mc_account.dto.*;
import com.social.mc_account.model.Account;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {
    Account toAccountMeDto(AccountMeDTO accountMeDTO);
    AccountMeDTO toAccountMeDtoAccount(Account account);
    List<AccountMeDTO> toAccountsMeDto(List<Account> accounts);
    AccountDataDTO toAccountDataDto(Account account);
    List<AccountPageDTO> toPageDtoAccounts(List<Account> accountsDto);
}
