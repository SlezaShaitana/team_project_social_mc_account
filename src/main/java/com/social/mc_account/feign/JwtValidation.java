package com.social.mc_account.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "JwtValidation", url = "http://localhost:8086")
public interface JwtValidation {

    @RequestMapping(method = RequestMethod.GET, value = "/api/v1/auth/check-validation")
    Boolean validateToken(@RequestParam("token") String token);

}
