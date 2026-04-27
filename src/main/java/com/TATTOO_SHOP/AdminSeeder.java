package com.TATTOO_SHOP;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.TATTOO_SHOP.entity.Admin;
import com.TATTOO_SHOP.repository.AdminRepository;

@Configuration
public class AdminSeeder {

    @Bean
    CommandLineRunner seedAdmin(AdminRepository repo) {
        return args -> {

            if (repo.count() == 0) {

                Admin admin = new Admin();
                admin.setUsername("karthi");
                admin.setPassword("karthi@321");

                repo.save(admin);

                System.out.println("Default admin inserted.");
            }
        };
    }
}