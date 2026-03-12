package com.timebottle.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EntityScan(basePackages = "com.timebottle.backend.entity")
@EnableJpaRepositories(basePackages = "com.timebottle.backend.repository")
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
    }
    
    @Configuration
    public static class WebConfig implements WebMvcConfigurer {
        @Override
        public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
            // 暴露Usersimg文件夹为静态资源
            registry.addResourceHandler("/Usersimg/**")
                    .addResourceLocations("file:C:/Project/TimeBottle/Usersimg/");
        }
    }
}