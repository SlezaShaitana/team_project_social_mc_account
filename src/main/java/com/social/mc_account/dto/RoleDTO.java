package com.social.mc_account.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
public class RoleDTO {
    private UUID id;
    private String role;
}
