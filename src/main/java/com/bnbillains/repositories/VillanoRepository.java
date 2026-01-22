package com.bnbillains.repositories;

import com.bnbillains.entities.Villano;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillanoRepository extends JpaRepository<Villano, Long> {

    // --- BÚSQUEDAS EXACTAS (Login y Perfil) ---
    // Sirve tanto para Login DB como para comprobar OAuth2
    Optional<Villano> findByEmail(String email);

    // Método necesario para el Login clásico por formulario (si usas username en vez de email)
    Optional<Villano> findByUsername(String username);

    Optional<Villano> findByCarnetDeVillano(String carnetDeVillano);

    // --- VALIDACIONES (Registro) ---
    // Sirve para el registro manual y para el Handler de OAuth2
    boolean existsByEmail(String email);
    boolean existsByCarnetDeVillano(String carnetDeVillano);

    // --- BUSCADOR INTELIGENTE ---
    List<Villano> findByNombreContainingIgnoreCaseOrAliasContainingIgnoreCase(String nombre, String alias, Sort sort);

}