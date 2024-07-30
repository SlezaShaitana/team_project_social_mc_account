package com.social.mc_account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.social.mc_account.dto", "com.social.mc_account.model", "com.social.mc_account.mapper"})
@EnableJpaRepositories(basePackages = "com.social.mc_account.repository")
@EnableDiscoveryClient
public class McAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(McAccountApplication.class, args);
	}
}