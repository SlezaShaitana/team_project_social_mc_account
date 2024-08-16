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
    @Column(unique = true, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String first_name;

    @Column(nullable = false)
    private String last_name;

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

    private String emoji_status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDate create_on;

    @UpdateTimestamp
    private LocalDate update_on;

    private LocalDate deletion_timestamp;

    @Column(nullable = false)
    private boolean isDeleted;
}