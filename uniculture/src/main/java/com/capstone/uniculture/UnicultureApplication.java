package com.capstone.uniculture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class UnicultureApplication {
	public static void main(String[] args) {
		SpringApplication.run(UnicultureApplication.class, args);
	}

}
