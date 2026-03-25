package com.timebottle.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:8080", "http://127.0.0.1:5173", "http://127.0.0.1:8080"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/user/**").permitAll()
                .requestMatchers("/api/admin/**").permitAll()
                .requestMatchers("/api/categories/**").permitAll()
                .requestMatchers("/api/bills/**").permitAll()
                .requestMatchers("/api/project-configs/**").permitAll()
                .requestMatchers("/api/daily-performances/**").permitAll()
                .requestMatchers("/api/overtime-records/**").permitAll()
                .requestMatchers("/api/salary-records/**").permitAll()
                .requestMatchers("/api/points/**").permitAll()
                .requestMatchers("/api/password-reset/**").permitAll()
                .requestMatchers("/api/cards/**").permitAll()
                .requestMatchers("/api/consignments/**").permitAll()
                .requestMatchers("/api/exam/**").permitAll()
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