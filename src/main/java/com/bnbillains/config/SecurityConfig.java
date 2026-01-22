package com.bnbillains.config;

import com.bnbillains.services.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Inyectamos TU servicio de usuarios (el que conecta con la tabla Villano)
    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // 1. RUTAS PÚBLICAS (Login, Recursos estáticos, Errores)
                        .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**", "/error/**").permitAll()

                        // 2. RUTAS PROTEGIDAS POR ROL (Ejemplos para cuando metáis roles)
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 3. RESTO BLOQUEADO
                        .anyRequest().authenticated()
                )
                // --- CONFIGURACIÓN LOGIN BASE DE DATOS (Tu parte) ---
                .formLogin(form -> form
                        .loginPage("/login")        // Tu vista personalizada
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                // --- CONFIGURACIÓN OAUTH2 (Google + Sitio para LinkedIn/X) ---
                // Al dejarlo así, Spring detectará automáticamente Google, LinkedIn y X
                // cuando se añadan al application.properties. ¡Tus compañeros no tendrán que tocar Java!
                .oauth2Login(oauth -> oauth
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                );

        return http.build();
    }

    // Conecta Spring Security con tu Base de Datos MySQL
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
