package com.bnbillains.repositories;

import com.bnbillains.entities.Reserva;
import com.bnbillains.entities.Villano;
import com.bnbillains.entities.Guarida;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByVillano_Id(Long villanoId, Sort sort);

    List<Reserva> findByGuarida_Id(Long guaridaId, Sort sort);

    List<Reserva> findByEstado(Boolean estado, Sort sort);


    List<Reserva> findByVillano_Id(Long villanoId);

    List<Reserva> findByGuarida_Id(Long guaridaId);

    // Existe alguna reserva asociada a esta factura?
    boolean existsByFactura_Id(Long facturaId);
}
