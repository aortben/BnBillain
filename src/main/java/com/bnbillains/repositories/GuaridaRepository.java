package com.bnbillains.repositories;

import com.bnbillains.entities.Guarida;
import com.bnbillains.entities.SalaSecreta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuaridaRepository extends JpaRepository<Guarida, Long> {

    // --- MÉTODO FALTANTE (IMPORTANTE) ---
    // Este es el que usa SalaSecretaService para encontrar la guarida y romper el vínculo
    Optional<Guarida> findBySalaSecreta(SalaSecreta salaSecreta);

    // Buscar por ubicación exacta
    List<Guarida> findByUbicacion(String ubicacion);

    // Buscar por nombre (contiene texto e ignora mayúsculas/minúsculas)
    List<Guarida> findByNombreContainingIgnoreCase(String nombre);

    // Buscar por rango de precio
    List<Guarida> findByPrecioNocheBetween(Double min, Double max);

    // Buscar por rango de precio con ordenación
    List<Guarida> findByPrecioNocheBetween(Double min, Double max, Sort sort);

    // Verificar si existe por nombre
    boolean existsByNombre(String nombre);
}

