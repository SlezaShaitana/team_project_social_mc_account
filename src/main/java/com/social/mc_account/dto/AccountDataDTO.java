package com.social.mc_account.dto;

import lombok.*;
import java.util.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDataDTO {
    private UUID id;
    private boolean isDeleted;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private List<AuthorityDTO> authorities;
}