package com.social.mc_account.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "JwtValidation", url = "http://localhost:8086/api/v1/auth")
public interface JwtValidation {
    @GetMapping("/check-validation") // Используйте @GetMapping и @RequestParam
    Boolean validateToken(@RequestParam("token") String token);
}