package com.pharmacy.inventory_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication

public class InventoryServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApplication.class, args);
		System.out.println("Inventory Service is running...");
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}
