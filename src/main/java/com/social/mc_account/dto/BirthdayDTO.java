package com.social.mc_account.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BirthdayDTO {
    private String firstName;
    private String lastName;
}