package com.kindsonthegenius.product_app;

import com.kindsonthegenius.product_app.model.User;
import com.kindsonthegenius.product_app.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ProductAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductAppApplication.class, args);
	}

	@Bean
	CommandLineRunner seedUsers(UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			createUserIfMissing(userRepository, passwordEncoder,
					"Admin", "User", "admin", "root");
			createUserIfMissing(userRepository, passwordEncoder,
					"Demo", "User", "demo", "demo");
		};
	}

	private void createUserIfMissing(UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder, String firstName,
			String lastName, String username, String password) {
		if (userRepository.findByUsername(username) == null) {
			User user = new User();
			user.setFirstname(firstName);
			user.setLastname(lastName);
			user.setUsername(username);
			user.setPasswordHash(passwordEncoder.encode(password));
			userRepository.save(user);
		}
	}

}
