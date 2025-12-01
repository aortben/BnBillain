package com.bnbillains.repositories;

import com.bnbillains.entities.Guarida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Sort;

@Repository
public interface GuaridaRepository extends JpaRepository<Guarida, Long> {

    // Métodos que ya usaba tu compañero
    List<Guarida> findByUbicacion(String ubicacion);
    List<Guarida> findByNombreContainingIgnoreCase(String text);
    // ESTE ES EL CRÍTICO: Debes declararlo para que acepte 'Sort'
    List<Guarida> findByPrecioNocheBetween(Double min, Double max, Sort sort);
    // Versión sin ordenar (por si acaso)
    List<Guarida> findByPrecioNocheBetween(Double min, Double max);
    boolean existsByNombre(String nombre);
}

