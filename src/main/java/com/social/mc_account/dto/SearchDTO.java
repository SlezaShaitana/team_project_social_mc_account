package com.social.mc_account.dto;

import lombok.Builder;
import lombok.Data;
import java.util.*;

@Data
@Builder
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
    private String statusCode;
    private int ageTo;
    private int ageFrom;
}