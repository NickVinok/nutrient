package com.nutrient.nutrientSpring;

import com.nutrient.nutrientSpring.Services.UserDetailsServiceImpl;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;

@SpringBootApplication
public class NutrientSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(NutrientSpringApplication.class, args);
	}
	@Bean
	public UserDetailsService getUserDetailsService(){
		return new UserDetailsServiceImpl();
	}

}
