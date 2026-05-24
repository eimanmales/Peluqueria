package com.ProgramacionV.Peluqueria.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Permite que las imágenes subidas por el usuario sean accesibles
     * desde el navegador en la ruta /uploads/servicios/nombre.jpg
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String carpetaAbsoluta = Paths.get("uploads").toAbsolutePath().toUri().toString();
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(carpetaAbsoluta);
    }
}
