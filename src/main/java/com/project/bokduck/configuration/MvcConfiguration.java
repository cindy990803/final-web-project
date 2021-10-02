package com.project.bokduck.configuration;

import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

public class MvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 클래스패스가 아닌, 외부 디렉토리의 자원을 웹에 노출해야 한다면..
        // 그 디렉토리를 리소스 폴더로 등록
        String dirName = "image";

        Path uploadDir = Paths.get(dirName);

        String uploadPath = uploadDir.toFile().getAbsolutePath();

        registry.addResourceHandler( "/" + dirName + "/**")
                .addResourceLocations("file:/"+ uploadPath + "/");
    }

}
