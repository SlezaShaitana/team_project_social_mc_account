package com.social.mc_account.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.time.LocalDate;
import java.util.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountMeDTO {
    private UUID id;
    @JsonProperty("deleted")
    private boolean isDeleted;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
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
    private LocalDate regDate;
    @JsonProperty("birthDate")
    private LocalDate birthDate;
    @JsonProperty("messagePermission")
    private String messagePermission;
    @JsonProperty("lastOnlineTime")
    private LocalDate lastOnlineTime;
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

    @JsonSetter("birthDate")
    public void setBirthDate(String birthDate) {
        if (!"none".equalsIgnoreCase(birthDate)) {
            try {
                this.birthDate = LocalDate.parse(birthDate);
            } catch (Exception e) {
                System.err.println("Invalid date format, value ignored: " + birthDate);
            }
        }
    }
}