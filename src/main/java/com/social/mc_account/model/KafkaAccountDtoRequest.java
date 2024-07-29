package com.social.mc_account.model;

import com.social.mc_account.dto.StatusRole;
import lombok.Data;

import java.util.UUID;

@Data
public class KafkaAccountDtoRequest {
    private UUID id;
    private String email;
    private String password;
    private StatusRole role;
}
