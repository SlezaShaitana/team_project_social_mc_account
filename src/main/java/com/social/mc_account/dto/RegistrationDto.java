package com.social.mc_account.dto;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class RegistrationDto {
    private UUID uuid;
    private boolean deleted;
    private String email;
    private String password1;
    private String password2;
    private String firstName;
    private String lastName;
    private String captchaSecret;
    private Role role;
}