package com.TATTOO_SHOP.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable())

            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                    "/",
                    "/index",
                    "/home",
                    "/tattoos",
                    "/book/**",
                    "/booking-status",
                    "/booking-status/**",
                    "/booking-success",
                    "/admin/login",
                    "/admin/logout",
                    "/css/**",
                    "/js/**",
                    "/images/**",
                    "/uploads/**",
                    "/webjars/**"
                ).permitAll()

                // Public APIs
                .requestMatchers(
                    "/api/admin/login",
                    "/api/tattoos/**",
                    "/api/bookings/**"
                ).permitAll()

                // Protected APIs
                .requestMatchers("/api/admin/**").authenticated()

                .anyRequest().permitAll()
            )

            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Dummy — JWT handles auth; this prevents Spring Security auto-config warnings
        return new InMemoryUserDetailsManager(
            User.withUsername("_unused_")
                .password("{noop}unused")
                .roles("NONE")
                .build()
        );
    }
}
