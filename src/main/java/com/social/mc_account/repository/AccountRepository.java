package com.social.mc_account.repository;

import com.social.mc_account.dto.AccountPageDTO;
import com.social.mc_account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
    List<AccountPageDTO> findByStatusCode(String statusCode);
    List<AccountPageDTO> findAllByStatusCode(String statusCode);
}
