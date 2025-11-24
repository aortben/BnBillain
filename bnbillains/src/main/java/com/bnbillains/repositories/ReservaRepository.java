package com.bnbillains.repositories;

import com.bnbillains.entities.Reserva;
import com.bnbillains.entities.Villano;
import com.bnbillains.entities.Guarida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByVillano(Villano villano);
    List<Reserva> findByGuarida(Guarida guarida);
    List<Reserva> findByEstado(Boolean estado);
    List<Reserva> findByFechaInicio(LocalDate fechaInicio);
    Optional<Reserva> findByFactura_Id(Long facturaId);
    boolean existsByFactura_Id(Long facturaId);
}
