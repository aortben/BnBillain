package com.bnbillains.repositories;

import com.bnbillains.entities.Reserva;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByGuarida_Id(Long guaridaId, Sort sort);

    List<Reserva> findByEstado(Boolean estado, Sort sort);


    List<Reserva> findByVillano_Id(Long villanoId, Sort sort);

    List<Reserva> findByGuarida_Id(Long guaridaId);

    // Existe alguna reserva asociada a esta factura?
    boolean existsByFactura_Id(Long facturaId);

    // Lógica: (StartA <= EndB) y (EndA >= StartB) es la fórmula matemática del choque
    @Query("SELECT r FROM Reserva r WHERE r.guarida.id = :guaridaId " +
            "AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    List<Reserva> encontrarConflictos(@Param("guaridaId") Long guaridaId,
                                      @Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    // Es igual, pero añadimos "AND r.id != :reservaId" para que no choque consigo misma
    @Query("SELECT r FROM Reserva r WHERE r.guarida.id = :guaridaId " +
            "AND r.id != :reservaId " +
            "AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    List<Reserva> encontrarConflictosParaActualizar(@Param("guaridaId") Long guaridaId,
                                                    @Param("reservaId") Long reservaId,
                                                    @Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin);
}
