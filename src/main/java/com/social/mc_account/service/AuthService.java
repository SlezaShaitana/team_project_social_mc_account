package com.social.mc_account.service;

import com.social.mc_account.model.KafkaAccountDtoRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class AuthService {
    private final RestTemplate restTemplate;

    public boolean validateToken(String token){
        String url = "/api/v1/auth/validation";
        Boolean isValid = restTemplate.postForObject(url, token, Boolean.class);
        return isValid != null && isValid;
    }

    public KafkaAccountDtoRequest getUserInfoFromToken(String token){
        String url = "http://api/v1/auth/login";
        return restTemplate.postForObject(url, token, KafkaAccountDtoRequest.class);
    }
}
