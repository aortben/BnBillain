package com.bnbillains.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value; // Importante
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Clase de configuración para habilitar la gestión de recursos estáticos en Spring MVC.
 * Permite servir archivos (imágenes) desde un directorio externo configurado.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Logger para registrar eventos importantes
    private static final Logger logger = LoggerFactory.getLogger(WebConfig.class);

    // Inyectamos el valor de UPLOAD_PATH desde application.properties
    @Value("${UPLOAD_PATH}")
    private String uploadPath;

    /**
     * Configura los manejadores de recursos estáticos.
     * Mapea la URL "/uploads/**" a la carpeta física definida en UPLOAD_PATH.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Verificar si la variable UPLOAD_PATH está configurada
        if (uploadPath != null && !uploadPath.isEmpty()) {

            // Convertimos la ruta relativa/absoluta a una ruta absoluta del sistema
            Path path = Paths.get(uploadPath);
            String absolutePath = path.toFile().getAbsolutePath();

            logger.info("Configurando recursos estáticos. URL: /uploads/** -> Carpeta Física: {}", absolutePath);

            // Configurar Spring para servir archivos
            // "file:/" + absolutePath + "/" es el formato que necesita Spring
            registry.addResourceHandler("/uploads/**")
                    .addResourceLocations("file:/" + absolutePath + "/");
        } else {
            logger.error("La variable UPLOAD_PATH no está configurada en application.properties.");
        }
    }
}