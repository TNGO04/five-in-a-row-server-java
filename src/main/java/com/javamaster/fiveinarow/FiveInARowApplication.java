package com.javamaster.fiveinarow;

//import com.javamaster.fiveinarow.repositories.UserRepository;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.javamaster.fiveinarow.repositories")
public class FiveInARowApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiveInARowApplication.class, args);
	}

}
