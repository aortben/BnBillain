package com.bnbillains.repositories;

import com.bnbillains.entities.Guarida;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Sort;

@Repository
public interface GuaridaRepository extends JpaRepository<Guarida, Long> {

    // --- MÉTODOS QUE USAN TUS COMPAÑEROS (Retornan List) ---
    List<Guarida> findByUbicacion(String ubicacion);
    List<Guarida> findByNombreContainingIgnoreCase(String text);
    List<Guarida> findByPrecioNocheBetween(Double min, Double max);
    // findAll(Sort) ya viene incluido en JpaRepository, no hace falta ponerlo explícito

    // --- MÉTODOS "PRO" (Retornan Page para cuando implementéis paginación) ---
    // Nota: Es mejor tener la opción preparada.
    Page<Guarida> findByNombreContainingIgnoreCase(String text, Pageable pageable);
    Page<Guarida> findByPrecioNocheBetween(Double min, Double max, Pageable pageable);

    // --- VALIDACIONES ---
    boolean existsByNombre(String nombre);
}

