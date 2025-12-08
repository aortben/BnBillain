package com.bnbillains.services;

import com.bnbillains.entities.Factura;
import com.bnbillains.repositories.FacturaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    // Métodos de lectura básicos (pasan el Sort al repositorio)
    public List<Factura> obtenerTodas(Sort sort) {
        return facturaRepository.findAll(sort);
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    // --- LÓGICA DE ESCRITURA ---

    // Guarda una nueva factura aplicando reglas de negocio
    public Factura guardar(Factura factura) {
        // Regla de oro: Una reserva solo puede tener UNA factura.
        // Si intentamos crear una nueva para una reserva que ya tiene, bloqueamos.
        if (factura.getId() == null && factura.getReserva() != null) {
            if (facturaRepository.existsByReserva_Id(factura.getReserva().getId())) {
                throw new IllegalArgumentException("Esta reserva ya ha sido facturada. ¡No seas avaricioso!");
            }
        }
        return facturaRepository.save(factura);
    }

    // --- MÉTODO ACTUALIZAR BLINDADO ---
    // Este método es "defensivo": protege los datos sensibles de ser borrados por error
    public Factura actualizar(Long id, Factura facturaDatosNuevos) {
        return facturaRepository.findById(id)
                .map(facturaExistente -> {
                    // 1. Actualizamos lo que el usuario quiere cambiar (Método de Pago)
                    facturaExistente.setMetodoPago(facturaDatosNuevos.getMetodoPago());

                    // 2. Actualizamos importes SOLO si vienen datos reales (no nulos)
                    if (facturaDatosNuevos.getImporte() != null) {
                        facturaExistente.setImporte(facturaDatosNuevos.getImporte());
                    }
                    if (facturaDatosNuevos.getImpuestosMalignos() != null) {
                        facturaExistente.setImpuestosMalignos(facturaDatosNuevos.getImpuestosMalignos());
                    }

                    // 3. SEGURIDAD CRÍTICA: NO tocamos la reserva (facturaExistente.setReserva(...))
                    // Mantenemos la relación que ya existe en la base de datos.
                    // Si el formulario web enviase la reserva como null, aquí la perderíamos si no hiciéramos esto.

                    return facturaRepository.save(facturaExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
    }

    public void eliminar(Long id) {
        facturaRepository.deleteById(id);
    }

    // --- BÚSQUEDA ---

    // Busca por método de pago (ej: "Bitcoin") y ordena
    public List<Factura> buscarPorMetodoPago(String metodo, Sort sort) {
        return facturaRepository.findByMetodoPagoContainingIgnoreCase(metodo, sort);
    }

    // Busca por rango de dinero y ordena
    public List<Factura> buscarPorRangoImporte(Double min, Double max, Sort sort) {
        return facturaRepository.findByImporteBetween(min, max, sort);
    }

    // Busca las facturas de un cliente concreto
    public List<Factura> obtenerFacturasPorVillano(Long villanoId) {
        return facturaRepository.findByReserva_Villano_Id(villanoId);
    }
}