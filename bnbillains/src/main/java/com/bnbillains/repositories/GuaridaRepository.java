package com.bnbillains.repositories;

import com.bnbillains.entities.Guarida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.domain.Sort;

@Repository
public interface GuaridaRepository extends JpaRepository<Guarida, Long> {

    List<Guarida> findByUbicacion(String ubicacion);
    List<Guarida> findByNombreContainingIgnoreCase(String text);
    List<Guarida> findByPrecioNocheBetween(Double min, Double max);
    boolean existsByNombre(String nombre);
    List<Guarida> findByPrecioNocheBetween(Double min, Double max, Sort sort);
    List<Guarida> findAll(Sort sort);
}

