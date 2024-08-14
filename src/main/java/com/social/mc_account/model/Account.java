package com.social.mc_account.model;

import com.social.mc_account.dto.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private String profile_cover;

    private String about;

    private String city;

    private String country;

    @Column(name = "status_code")
    private String status_code;

    @CreatedDate
    @Column(updatable = false)
    private LocalDate reg_date;

    private LocalDate birth_date;

    private String message_permission;

    private LocalDate last_online_time;

    private boolean is_online;

    private boolean is_blocked;

    @Column(name = "emoji_status")
    private String emoji_status;

    @CreationTimestamp
    @Column(name = "create_on", updatable = false)
    private LocalDate createdOn;

    @UpdateTimestamp
    @Column(name = "update_on")
    private LocalDate updatedOn;

    private LocalDate deletion_timestamp;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}