package com.colmena.videojuegos;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ImagenConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry){
        WebMvcConfigurer.super.addResourceHandlers(registry);
        String uploadDir = System.getProperty("os.name").toLowerCase().contains("win")
                ? "file:///C:/Videojuegos/imagenes/"
                : "file://" + System.getProperty("user.home") + "/Videojuegos/imagenes/";
        registry.addResourceHandler("/imagenes/**").addResourceLocations(uploadDir);
    }

}
