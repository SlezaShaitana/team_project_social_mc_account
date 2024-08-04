package com.social.mc_account.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
public class AccountDataDTO {
    private UUID id;
    private boolean isDeleted;
    private String firstName;
    private String email;
    private String role;
    private List<AuthorityDTO> authorities;
}