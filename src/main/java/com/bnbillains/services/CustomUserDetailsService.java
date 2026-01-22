package com.bnbillains.services;

import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.VillanoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private VillanoRepository villanoRepository;

    /**
     * Carga el usuario desde la base de datos usando el repositorio.
     * Tal como indica el PDF UD05-2, convertimos nuestra entidad en un UserDetails.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Buscamos por username (para el login de formulario normal)
        Villano villano = villanoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Villano no encontrado: " + username));

        // Construimos el objeto User de Spring Security
        // NOTA: Spring añade "ROLE_" automáticamente, así que si en BD tienes "ROLE_ADMIN",
        // aquí a veces es mejor pasarlo limpio o usar authorities directas.
        // Para simplificar y seguir el estilo del PDF, lo hacemos así:

        return User.builder()
                .username(villano.getUsername())
                .password(villano.getPassword())
                .authorities(villano.getRole()) // Usamos authorities para pasar el rol completo (ej: ROLE_ADMIN)
                .disabled(!villano.isEnabled()) // Si enabled es false, disabled es true
                .build();
    }
}