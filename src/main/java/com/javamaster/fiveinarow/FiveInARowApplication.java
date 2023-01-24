package com.javamaster.fiveinarow;

//import com.javamaster.fiveinarow.repositories.UserRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.javamaster.fiveinarow.repositories")
public class FiveInARowApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiveInARowApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurer () {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**")
								.allowedMethods("GET", "POST", "PUT", "DELETE")
								.allowedHeaders("*")
								.allowedOrigins("http://d2rhazkk8xgu7j.cloudfront.net");
			}
		};
	}

}
