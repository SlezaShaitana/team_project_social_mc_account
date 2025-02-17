package com.social.mc_account.repository;

import com.social.mc_account.dto.AccountPageDTO;
import com.social.mc_account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<Account> findByStatusCode(String statusCode);
    List<AccountPageDTO> findAllByStatusCode(String statusCode);
    Account findByEmail(String email);
    List<Account> findAccountsById(UUID uuid);
}
