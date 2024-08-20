package com.social.mc_account.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@FeignClient(value = "StorageClient", url = "http://79.174.80.200:8092/")
public interface StorageClient {
    @PostMapping("/api/v1/storage")
    String pathForImage(@RequestParam("file") MultipartFile file);
}

