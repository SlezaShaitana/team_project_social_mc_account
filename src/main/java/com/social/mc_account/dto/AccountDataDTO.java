package com.social.mc_account.dto;

import com.social.mc_account.dto.AuthorityDTO;
import com.social.mc_account.dto.RoleDTO;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class AccountDataDTO {
    private UUID id;
    private boolean isDeleted;
    private String firstName;
    private String email;
    private String password;
    private List<RoleDTO> roles;
    private List<AuthorityDTO> authorities;
}
