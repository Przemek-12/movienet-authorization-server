package com.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@EnableAuthorizationServer
public class MovieNetAuthorizationServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieNetAuthorizationServiceApplication.class, args);
	}

}
