package com.bnbillains.repositories;

import com.bnbillains.entities.Villano;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillanoRepository extends JpaRepository<Villano, Long> {

    // Búsquedas exactas para login o recuperación de perfil
    Optional<Villano> findByEmail(String email);
    Optional<Villano> findByCarnetDeVillano(String carnetDeVillano);

    // Validaciones de unicidad (se usan al registrar un nuevo villano)
    boolean existsByEmail(String email);
    boolean existsByCarnetDeVillano(String carnetDeVillano);

    // --- BUSCADOR INTELIGENTE ---
    // Esta consulta es "Nombre O Alias". Permite que el usuario escriba algo
    // y el sistema busque en ambos campos a la vez.
    List<Villano> findByNombreContainingIgnoreCaseOrAliasContainingIgnoreCase(String nombre, String alias, Sort sort);
}