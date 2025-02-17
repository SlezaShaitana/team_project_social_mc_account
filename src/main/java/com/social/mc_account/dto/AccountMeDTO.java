package com.social.mc_account.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class AccountMeDTO {
        private UUID id;
        private boolean isDeleted;
        private String firstName;
        private String lastName;
        private String email;
        private String password;
        private String phone;
        private String photo;
        private String profileCover;
        private String about;
        private String city;
        private String country;
        private String statusCode;
        private Date regDate;
        private Date birthDate;
        private String messagePermission;
        private Date lastOnlineTime;
        private boolean isOnline;
        private boolean isBlocked;
        private String emojiStatus;
        private LocalDateTime createdOn;
        private LocalDateTime updatedOn;
        private LocalDateTime deletionTimestamp;
}