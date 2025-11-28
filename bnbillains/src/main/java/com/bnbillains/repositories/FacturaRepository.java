package com.bnbillains.repositories;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Reserva;
import com.bnbillains.entities.Villano;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    Optional<Factura> findByReserva(Reserva reserva);
    Optional<Factura> findByReserva_Id(Long reservaId);
    Optional<Factura> findByVillano(Villano villano);
    Optional<Factura> findByVillano_Id(Long villanoId);
    List<Factura> findByFechaEmision(LocalDate fechaEmision);
    List<Factura> findByMetodoPagoContainingIgnoreCase(String metodo);
    boolean existsByReserva_Id(Long reservaId);
    List<Factura> findByFechaEmisionBetween(LocalDate inicio, LocalDate fin);
}

