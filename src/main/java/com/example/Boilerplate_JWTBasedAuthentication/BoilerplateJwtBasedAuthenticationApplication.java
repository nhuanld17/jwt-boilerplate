package com.example.Boilerplate_JWTBasedAuthentication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BoilerplateJwtBasedAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BoilerplateJwtBasedAuthenticationApplication.class, args);
	}

}
