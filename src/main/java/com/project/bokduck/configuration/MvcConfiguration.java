package com.project.bokduck.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @Author MunKyoung
 * 디렉토리 경로 설정
 *
 */
@Configuration
public class MvcConfiguration implements WebMvcConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String dirName = "review_images";

        Path uploadDir = Paths.get(dirName);

        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler( "/" + dirName + "/**")
                .addResourceLocations("file:/"+ uploadPath + "/");
    }
}