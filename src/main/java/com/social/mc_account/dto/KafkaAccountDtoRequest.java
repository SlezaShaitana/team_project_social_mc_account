package com.social.mc_account.dto;

import com.social.mc_account.dto.Role;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

@Data
public class KafkaAccountDtoRequest {
    private UUID id;
    private String email;
    private Role role;
}
