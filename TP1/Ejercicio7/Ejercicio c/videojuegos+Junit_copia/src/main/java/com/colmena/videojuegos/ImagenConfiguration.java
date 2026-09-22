package com.colmena.videojuegos;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class ImagenConfiguration implements WebMvcConfigurer {

    private final Path uploadDir;

    public ImagenConfiguration(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.uploadDir = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.uploadDir);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler("/imagenes/**")
                .addResourceLocations("file:" + this.uploadDir.toString() + "/");
    }
}
