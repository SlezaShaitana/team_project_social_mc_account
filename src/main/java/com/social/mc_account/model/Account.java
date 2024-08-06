package com.social.mc_account.model;

import com.social.mc_account.dto.Role;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Builder
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private String phone;

    private String photo;

    @Column(name = "profile_cover")
    private String profileCover;

    private String about;

    private String city;

    private String country;

    @Column(name = "status_code")
    private String statusCode;

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    private LocalDate regDate;

    private LocalDate birthDate;

    @Column(name = "message_permission")
    private String messagePermission;

    @Column(name = "last_online_time")
    private LocalDate lastOnlineTime;

    @Column(name = "is_online")
    private boolean isOnline;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "emoji_status")
    private String emojiStatus;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDate createdOn;

    @UpdateTimestamp
    @Column(name = "update_on")
    private LocalDate updatedOn;

    @Column(name = "deletion_timestamp")
    private LocalDate deletionTimestamp;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}