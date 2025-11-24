package com.bnbillains.repositories;

import com.bnbillains.entities.Guarida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GuaridaRepository extends JpaRepository<Guarida, Long> {
    List<Guarida> findByUbicacion(String ubicacion);
    List<Guarida> findByNombreContainingIgnoreCase(String text);
    List<Guarida> findByPrecioNocheBetween(Double min, Double max);
    boolean existsByNombre(String nombre);
}

