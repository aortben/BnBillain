package com.bnbillains.repositories;

import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Villano;
import com.bnbillains.entities.Guarida;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // --- BÚSQUEDAS ORDENADAS ---
    List<Resena> findByPuntuacion(Long puntuacion, Sort sort);

    // Buscar por texto en el comentario + orden
    List<Resena> findByComentarioContainingIgnoreCase(String texto, Sort sort);

}

