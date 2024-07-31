package com.social.mc_account.model;

import com.social.mc_account.dto.StatusRole;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

@Data
public class KafkaAccountDtoRequest implements UserDetails {
    private UUID id;
    private String email;
    private String password;
    private StatusRole role;
    private Collection<? extends GrantedAuthority> authorities;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return email;
    }
}
