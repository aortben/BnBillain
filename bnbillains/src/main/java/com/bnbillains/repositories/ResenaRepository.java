package com.bnbillains.repositories;

import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Villano;
import com.bnbillains.entities.Guarida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByVillano(Villano villano);
    List<Resena> findByGuarida(Guarida guarida);
    List<Resena> findByPuntuacion(Long puntuacion);
    List<Resena> findByPuntuacionBetween(Long min, Long max);
    List<Resena> findByComentarioContainingIgnoreCase(String texto);

}

