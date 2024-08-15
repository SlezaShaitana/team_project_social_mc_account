package com.social.mc_account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class RegistrationDto {
    private UUID uuid = UUID.randomUUID();
    private boolean deleted;
    private String email;
    private String password1;
    private String password2;
    private String firstName;
    private String lastName;
    private String captchaSecret;
    private Role role = Role.USER;
}