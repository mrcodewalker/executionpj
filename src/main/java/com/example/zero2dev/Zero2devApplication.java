package com.example.zero2dev;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Zero2devApplication {
	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.directory(".")
				.filename("SYSTEM32.env")
				.load();
		System.setProperty("spring.datasource.url", dotenv.get("SPRING_DATASOURCE_URL", "jdbc:mysql://localhost:4306/zero2dev"));
		System.setProperty("spring.datasource.username", dotenv.get("SPRING_DATASOURCE_USERNAME", "root"));
		System.setProperty("spring.datasource.password", dotenv.get("SPRING_DATASOURCE_PASSWORD", "123456789"));
		System.setProperty("cors.allowed.origins", dotenv.get("CORS_ALLOWED_ORIGINS"));
		SpringApplication.run(Zero2devApplication.class, args);
	}

}
