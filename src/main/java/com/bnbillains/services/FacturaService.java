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

    public List<Factura> obtenerTodas(Sort sort) {
        return facturaRepository.findAll(sort);
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    // --- ESCRITURA ---
    public Factura guardar(Factura factura) {
        if (factura.getId() == null && factura.getReserva() != null) {
            if (facturaRepository.existsByReserva_Id(factura.getReserva().getId())) {
                throw new IllegalArgumentException("Esta reserva ya ha sido facturada. ¡No seas avaricioso!");
            }
        }
        return facturaRepository.save(factura);
    }

    // --- MÉTODO ACTUALIZAR BLINDADO ---
    public Factura actualizar(Long id, Factura facturaDatosNuevos) {
        return facturaRepository.findById(id)
                .map(facturaExistente -> {
                    // 1. Actualizamos SOLO lo que el usuario puede cambiar (Método de Pago)
                    facturaExistente.setMetodoPago(facturaDatosNuevos.getMetodoPago());

                    // 2. Protegemos los datos sensibles: Solo actualizamos si vienen datos reales
                    // (Esto evita que se pongan a 0 o null si el formulario no los envía)
                    if (facturaDatosNuevos.getImporte() != null) {
                        facturaExistente.setImporte(facturaDatosNuevos.getImporte());
                    }
                    if (facturaDatosNuevos.getImpuestosMalignos() != null) {
                        facturaExistente.setImpuestosMalignos(facturaDatosNuevos.getImpuestosMalignos());
                    }

                    // 3. ⛔ IMPORTANTE: NO tocamos la reserva (facturaExistente.setReserva(...))
                    // Mantenemos la relación que ya existe en la base de datos.
                    // Así, aunque el formulario envíe la reserva mal o null, no la perdemos.

                    return facturaRepository.save(facturaExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Factura no encontrada"));
    }

    public void eliminar(Long id) {
        facturaRepository.deleteById(id);
    }

    // --- BÚSQUEDA ---
    public List<Factura> buscarPorMetodoPago(String metodo, Sort sort) {
        return facturaRepository.findByMetodoPagoContainingIgnoreCase(metodo, sort);
    }

    public List<Factura> buscarPorRangoImporte(Double min, Double max, Sort sort) {
        return facturaRepository.findByImporteBetween(min, max, sort);
    }

    public List<Factura> obtenerFacturasPorVillano(Long villanoId) {
        return facturaRepository.findByReserva_Villano_Id(villanoId);
    }
}