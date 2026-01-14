package com.ryotube.application;

import jakarta.persistence.Entity;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
// @EnableJpaRepositories(basePackages = "com.ryotube.application.Repositories")
// @EntityScan(basePackages = "com.ryotube.application.Entities")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
