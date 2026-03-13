package com.timebottle.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors
                .configurationSource(request -> {
                    org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();
                    config.setAllowedOrigins(java.util.Arrays.asList("*"));
                    config.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(java.util.Arrays.asList("Content-Type", "Authorization"));
                    return config;
                })
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/user/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/bills/**").permitAll()
                .requestMatchers("/api/project-configs/**").permitAll()
                .requestMatchers("/api/daily-performances/**").permitAll()
                .requestMatchers("/api/overtime-records/**").permitAll()
                .requestMatchers("/Usersimg/**").permitAll()
                .requestMatchers("/default-avatar.svg").permitAll()
                .requestMatchers("/test.html").permitAll()
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                .permitAll()
            );

        return http.build();
    }
}