package com.social.mc_account.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class AuthorityDTO {
    private UUID id;
    private String authority;
}