package com.bnbillains.repositories;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Reserva;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {

    // --- FILTROS DE BÚSQUEDA Y ORDENACIÓN ---
    // Este método es el ejemplo perfecto de cómo buscamos y ordenamos a la vez.
    // Recibe el texto a buscar (metodo) y el criterio de orden (sort).
    List<Factura> findByMetodoPagoContainingIgnoreCase(String metodo, Sort sort);

    // Filtra facturas cuyo importe esté dentro de un rango (Min y Max) y las ordena.
    List<Factura> findByImporteBetween(Double min, Double max, Sort sort);

    // Recupera todas las facturas de un cliente específico buscando a través de la relación con Reserva.
    List<Factura> findByReserva_Villano_Id(Long villanoId);

    // --- MÉTODOS CRUCIALES PARA EL SISTEMA AUTOMÁTICO ---

    // Permite recuperar la factura sabiendo solo el ID de la reserva.
    // Es vital para que el botón "Ver Factura" funcione en el listado de reservas.
    Optional<Factura> findByReserva_Id(Long reservaId);

    // Validación de seguridad: Comprueba si existe factura para una reserva dada.
    // Se usa antes de crear una nueva para evitar duplicados.
    boolean existsByReserva_Id(Long reservaId);
}