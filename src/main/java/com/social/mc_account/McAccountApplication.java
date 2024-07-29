package com.social.mc_account;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@EntityScan(basePackages = {"com.social.mc_account.dto", "com.social.mc_account.model"})
@EnableJpaRepositories(basePackages = "com.social.mc_account.repository")
public class McAccountApplication {

	public static void main(String[] args) {
		SpringApplication.run(McAccountApplication.class, args);
	}
}