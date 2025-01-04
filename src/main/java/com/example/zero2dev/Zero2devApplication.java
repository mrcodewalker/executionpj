package com.example.zero2dev;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
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
		System.setProperty("GOOGLE_KEY", dotenv.get("GOOGLE_KEY"));
		System.setProperty("zero2dev.img_path", dotenv.get("IMG_PATH", "C:\\Users\\ADMIN\\Zero2Dev\\zero2dev\\src\\main\\resources\\images"));
		System.setProperty("zero2dev.banner_path", dotenv.get("BANNER_PATH", "C:\\Users\\ADMIN\\Zero2Dev\\zero2dev\\src\\main\\resources\\banners"));
		System.setProperty("zero2dev.font_path", dotenv.get("FONT_PATH", "C:\\Users\\ADMIN\\Zero2Dev\\zero2dev\\src\\main\\resources\\fonts"));


		System.setProperty("certificate.keystore.path", dotenv.get("KEY_STORE_PATH", "classpath:keystore/keystore.p12"));
		System.setProperty("certificate.keystore.password", dotenv.get("KEY_STORE_PASSWORD", "Haibeo2004@"));
		System.setProperty("certificate.key.alias", dotenv.get("KEY_ALIAS", "alias"));
		System.setProperty("certificate.key.password", dotenv.get("KEY_PASSWORD", "Haibeo2004@"));

		SpringApplication.run(Zero2devApplication.class, args);
	}

}
