package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Sample Embedded Mongo db run.
 *
 * http://localhost:8080/user/getAllUsers
 * http://localhost:8080/user/getAllUsersLeaky
 */
@SpringBootApplication
public class MongoDbDemo {
	@Autowired
	BusinessLogic businessLogic;

	public static void main(String[] args) {
		SpringApplication.run(MongoDbDemo.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("Running business logic...");
			businessLogic.run();
		};
	}
}
