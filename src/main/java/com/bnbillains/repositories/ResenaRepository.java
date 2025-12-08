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

    // Filtro exacto: Sirve para los botones de la web "Ver 5 Estrellas", "Ver 1 Estrella".
    // Acepta ordenación (por fecha, por ejemplo).
    List<Resena> findByPuntuacion(Long puntuacion, Sort sort);

    // Buscador de texto: Busca palabras dentro del comentario.
    // "IgnoreCase" permite encontrar "Malo" aunque escriban "malo".
    List<Resena> findByComentarioContainingIgnoreCase(String texto, Sort sort);

}