package com.social.mc_account.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class SearchDTO {
    private UUID id;
    private boolean isDeleted;
    private List<UUID> ids;
    private List<UUID> blockedByIds;
    private String author;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private boolean isBlocked;
    private StatusCode statusCode;
    private int ageTo;
    private int ageFrom;
}
