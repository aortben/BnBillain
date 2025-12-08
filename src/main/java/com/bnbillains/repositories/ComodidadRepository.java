package com.bnbillains.repositories;

import com.bnbillains.entities.Comodidad;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// Marcamos esto como un Repositorio para que Spring lo detecte
@Repository
public interface ComodidadRepository extends JpaRepository<Comodidad, Long> {

    // Búsqueda exacta por nombre
    Comodidad findByNombre(String nombre);

    // Lo mismo pero da igual mayúsculas o minúsculas
    Comodidad findByNombreIgnoreCase(String nombre);

    // Buscador simple: Encuentra si contiene el texto (para filtros)
    List<Comodidad> findByNombreContainingIgnoreCase(String fragmento);

    // El buscador principal: Filtra por texto y además ordena los resultados
    List<Comodidad> findByNombreContainingIgnoreCase(String fragmento, Sort sort);

    // Comprueba si existe el nombre (para validar duplicados antes de guardar)
    boolean existsByNombreIgnoreCase(String nombre);
}