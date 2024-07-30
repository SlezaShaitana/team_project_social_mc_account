package com.social.mc_account.model;

import com.social.mc_account.dto.StatusRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true)
    private UUID id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusRole role;

    @Column(nullable = false)
    private String phone;

    private String photo;

    @Column(name = "profile_cover")
    private String profileCover;

    private String about;

    private String city;

    private String country;

    @Column(name = "status_code")
    private String statusCode;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reg_date")
    private Date regDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "birthDate")
    private Date birthDate;

    @Column(name = "message_permission")
    private String messagePermission;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_online_time")
    private Date lastOnlineTime;

    @Column(name = "is_online")
    private boolean isOnline;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @Column(name = "emoji_status")
    private String emojiStatus;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private LocalDateTime createdOn;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_on")
    private LocalDateTime updatedOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deletion_timestamp")
    private LocalDateTime deletionTimestamp;
}
