package com.bnbillains.handlers;

import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.VillanoRepository;
import com.bnbillains.services.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private VillanoRepository villanoRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // 1. Obtener email de Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 2. Verificar existencia en BD (Adaptación lógica PDF a Google)
        Villano villano = null;
        if(email != null) {
            villano = villanoRepository.findByEmail(email).orElse(null);
        }

        if (villano == null) {
            // PDF: Si no existe, lanzamos excepción (que captura el FailureHandler)
            throw new OAuth2AuthenticationException("Email no registrado: " + email);
        }

        // 3. Cargar usuario real y autenticar
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(villano.getUsername());

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // 4. Éxito total
        response.sendRedirect("/");
    }
}