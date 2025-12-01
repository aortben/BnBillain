package com.bnbillains.repositories;

import com.bnbillains.entities.Villano;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VillanoRepository extends JpaRepository<Villano, Long> {

    Optional<Villano> findByEmail(String email);
    Optional<Villano> findByCarnetDeVillano(String carnetDeVillano);

    boolean existsByEmail(String email);
    boolean existsByCarnetDeVillano(String carnetDeVillano);

    // --- NUEVO PARA EL CONTROLLER ---
    // Busca por nombre O por alias (contiene texto, ignora mayúsculas) + ORDENACIÓN
    List<Villano> findByNombreContainingIgnoreCaseOrAliasContainingIgnoreCase(String nombre, String alias, Sort sort);
}

