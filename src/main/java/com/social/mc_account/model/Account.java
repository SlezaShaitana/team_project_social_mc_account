package com.social.mc_account.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@Table(name = "users")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private UUID id;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    private String email;

    private String password;

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
    @Column(name = "birth_date")
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
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_on", updatable = false)
    private Date createdOn;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_on")
    private Date updateOn;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deletion_timestamp")
    private Date deletionTimestamp;
}
