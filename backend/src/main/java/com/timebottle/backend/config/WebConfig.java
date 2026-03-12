package com.timebottle.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.avatar-path:./uploads/avatars}")
    private String avatarPath;

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // 确保目录存在
        File uploadDir = new File(avatarPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 获取绝对路径
        String absolutePath = uploadDir.getAbsolutePath();

        // 映射 /Usersimg/** 到上传目录
        registry.addResourceHandler("/Usersimg/**")
                .addResourceLocations("file:" + absolutePath + "/");
    }
}
