package com.TATTOO_SHOP.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Serve uploaded files from absolute uploads/ folder at project root
        String uploadsAbsPath = Paths.get(System.getProperty("user.dir"), "uploads")
                .toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadsAbsPath + "/")
                .setCachePeriod(3600);

        System.out.println("📁 Serving uploads from: " + uploadsAbsPath);
    }
}
