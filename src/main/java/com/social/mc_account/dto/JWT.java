package com.social.mc_account.dto;

import lombok.Data;

import java.util.List;

@Data
public class JWT {
    private String id;
    private String email;
    private List<String> roles;
}
