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

    // --- GESTIÓN DE RELACIONES ---

    // Método especial para encontrar a qué Guarida pertenece una Sala Secreta.
    // Útil para operaciones de mantenimiento o borrado seguro.
    Optional<Guarida> findBySalaSecreta(SalaSecreta salaSecreta);

    // --- BÚSQUEDAS Y FILTROS ---

    // Busca guaridas por ubicación exacta (ej: "Volcán", "Isla Desierta")
    List<Guarida> findByUbicacion(String ubicacion);

    // Buscador principal: Encuentra guaridas cuyo nombre contenga el texto (flexible)
    List<Guarida> findByNombreContainingIgnoreCase(String nombre);

    // Filtra guaridas dentro de un presupuesto (Precio Mínimo y Máximo)
    List<Guarida> findByPrecioNocheBetween(Double min, Double max);

    // Lo mismo que el anterior, pero permite ordenar los resultados (Baratos primero, etc.)
    List<Guarida> findByPrecioNocheBetween(Double min, Double max, Sort sort);

    // --- VALIDACIONES ---

    // Comprobación rápida para evitar que se creen dos guaridas con el mismo nombre
    boolean existsByNombre(String nombre);
}