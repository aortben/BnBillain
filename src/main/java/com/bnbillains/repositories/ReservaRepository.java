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

    // Busca todas las reservas de una guarida concreta y las ordena
    List<Reserva> findByGuarida_Id(Long guaridaId, Sort sort);

    // Filtra las reservas según si están confirmadas (true) o pendientes (false)
    List<Reserva> findByEstado(Boolean estado, Sort sort);

    // Busca el historial de reservas de un cliente (Villano)
    List<Reserva> findByVillano_Id(Long villanoId, Sort sort);

    // Versión simple sin ordenar (útil para validaciones rápidas)
    List<Reserva> findByGuarida_Id(Long guaridaId);

    // Comprueba si esta factura ya está pillada (para evitar duplicados)
    boolean existsByFactura_Id(Long facturaId);

    // Consulta personalizada (JPQL) para ver si las fechas chocan.
    // La lógica es: Si la nueva fecha empieza antes de que acabe la vieja
    // Y la nueva fecha acaba después de que empiece la vieja... ¡HAY CONFLICTO!
    @Query("SELECT r FROM Reserva r WHERE r.guarida.id = :guaridaId " +
            "AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    List<Reserva> encontrarConflictos(@Param("guaridaId") Long guaridaId,
                                      @Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);

    // Lo mismo que arriba, pero para cuando estamos EDITANDO una reserva.
    // Añadimos "AND r.id != :reservaId" para que la reserva no choque consigo misma
    // al comprobar las fechas en la base de datos.
    @Query("SELECT r FROM Reserva r WHERE r.guarida.id = :guaridaId " +
            "AND r.id != :reservaId " +
            "AND (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio)")
    List<Reserva> encontrarConflictosParaActualizar(@Param("guaridaId") Long guaridaId,
                                                    @Param("reservaId") Long reservaId,
                                                    @Param("fechaInicio") LocalDate fechaInicio,
                                                    @Param("fechaFin") LocalDate fechaFin);
}