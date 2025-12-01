package com.bnbillains.repositories;

import com.bnbillains.entities.Comodidad;
import org.springframework.data.domain.Sort; // IMPORTANTE
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComodidadRepository extends JpaRepository<Comodidad, Long> {

    Comodidad findByNombre(String nombre);
    Comodidad findByNombreIgnoreCase(String nombre);

    List<Comodidad> findByNombreContainingIgnoreCase(String fragmento);

    // busqueda y ordenacion
    List<Comodidad> findByNombreContainingIgnoreCase(String fragmento, Sort sort);

    boolean existsByNombreIgnoreCase(String nombre);
}