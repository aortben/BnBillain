package com.bnbillains.repositories;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Reserva;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    List<Factura> findByMetodoPagoContainingIgnoreCase(String metodo, Sort sort);

    // Buscar facturas por rango de importe (ej: de 1000 a 5000 monedas de oro)
    List<Factura> findByImporteBetween(Double min, Double max, Sort sort);

    List<Factura> findByReserva_Villano_Id(Long villanoId);

    // ¡CRUCIAL! Necesario para que ReservaService encuentre la factura asociada
    Optional<Factura> findByReserva_Id(Long reservaId);



    // --- MÉTODOS DE SOPORTE (validacion)---
    boolean existsByReserva_Id(Long reservaId);
}

