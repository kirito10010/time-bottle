package com.timebottle.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.avatar-path:uploads/avatars}")
    private String avatarPath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        Path path = Paths.get(avatarPath).toAbsolutePath();
        File uploadDir = path.toFile();
        
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String absolutePath = uploadDir.getAbsolutePath();

        registry.addResourceHandler("/Usersimg/**")
                .addResourceLocations("file:" + absolutePath + File.separator);
    }
}
