package com.social.mc_account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountMeDTO {
    private UUID id;
    @JsonProperty(value = "deleted")
    private boolean isDeleted;
    @JsonProperty(value = "firstName")
    private String firstName;
    @JsonProperty(value = "lastName")
    private String lastName;
    private String email;
    private String password;
    private Role role;
    private String phone;
    private String photo;
    @JsonProperty("profileCover")
    private String profileCover;
    private String about;
    private String city;
    private String country;
    @JsonProperty("statusCode")
    private String statusCode;
    @JsonProperty("regDate")
    private Date regDate;
    @JsonProperty("birthDate")
    private Date birthDate;
    @JsonProperty("messagePermission")
    private String messagePermission;
    @JsonProperty("lastOnlineTime")
    private Date lastOnlineTime;
    @JsonProperty("online")
    private boolean isOnline;
    @JsonProperty("blocked")
    private boolean isBlocked;
    @JsonProperty("emojiStatus")
    private String emojiStatus;
    @JsonProperty("createdOn")
    private LocalDateTime createdOn;
    @JsonProperty("updatedOn")
    private LocalDateTime updatedOn;
    @JsonProperty("deletionTimestamp")
    private LocalDateTime deletionTimestamp;

    public AccountMeDTO(UUID uuid, String mail, String john, String doe, String photoUrl, String aboutMe, Role role, boolean b, boolean b1) {
    }
}