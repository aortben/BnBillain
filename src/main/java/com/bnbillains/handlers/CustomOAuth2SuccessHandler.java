package com.bnbillains.handlers;

import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.VillanoRepository;
import com.bnbillains.services.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomOAuth2SuccessHandler.class);

    @Autowired
    private VillanoRepository villanoRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        try {
            // 1. Obtener email del proveedor OAuth2 (Google, Discord, GitLab)
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            
            // Log para depuración - ver todos los atributos disponibles
            Map<String, Object> attributes = oAuth2User.getAttributes();
            logger.info("OAuth2 User Attributes: {}", attributes);
            logger.info("OAuth2 User Name: {}", oAuth2User.getName());
            
            String email = null;
            
            // Intentar obtener email de diferentes formas según el proveedor
            if (attributes.containsKey("email")) {
                email = (String) attributes.get("email");
                logger.info("Email encontrado en atributo 'email': {}", email);
            } else if (attributes.containsKey("mail")) {
                email = (String) attributes.get("mail");
                logger.info("Email encontrado en atributo 'mail': {}", email);
            } else if (attributes.containsKey("emailAddress")) {
                email = (String) attributes.get("emailAddress");
                logger.info("Email encontrado en atributo 'emailAddress': {}", email);
            } else {
                // Para Discord, el email puede estar en un objeto anidado
                Object emailObj = attributes.get("email");
                if (emailObj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> emailMap = (Map<String, Object>) emailObj;
                    email = (String) emailMap.get("emailAddress");
                    logger.info("Email encontrado en objeto anidado: {}", email);
                }
            }
            
            // Si aún no hay email, intentar obtenerlo del nombre de usuario
            if (email == null || email.isEmpty()) {
                String username = oAuth2User.getName();
                logger.info("Intentando obtener email del username: {}", username);
                if (username != null && username.contains("@")) {
                    email = username;
                    logger.info("Email obtenido del username: {}", email);
                }
            }

            // 2. Verificar que tenemos un email
            if (email == null || email.isEmpty()) {
                logger.error("No se pudo obtener el email del proveedor OAuth2. Atributos disponibles: {}", attributes);
                throw new OAuth2AuthenticationException("No se pudo obtener el email del proveedor OAuth2");
            }

            // 3. Verificar existencia en BD
            Villano villano = villanoRepository.findByEmail(email).orElse(null);
            logger.info("Buscando villano con email: {}. Encontrado: {}", email, villano != null);

            if (villano == null) {
                // PDF: Si no existe, lanzamos excepción (que captura el FailureHandler)
                logger.warn("Email no registrado en BD: {}", email);
                throw new OAuth2AuthenticationException("Email no registrado: " + email);
            }

            // 4. Cargar usuario real y autenticar
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(villano.getUsername());
            logger.info("Usuario autenticado exitosamente: {}", villano.getUsername());

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 5. Éxito total
            response.sendRedirect("/");
            
        } catch (OAuth2AuthenticationException e) {
            logger.error("Error en autenticación OAuth2: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado en CustomOAuth2SuccessHandler", e);
            throw new OAuth2AuthenticationException("Error al procesar autenticación OAuth2: " + e.getMessage());
        }
    }
}