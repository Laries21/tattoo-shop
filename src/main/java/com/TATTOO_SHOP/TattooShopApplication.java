package com.TATTOO_SHOP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class TattooShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(TattooShopApplication.class, args);
	}

}
