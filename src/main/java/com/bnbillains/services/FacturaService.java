package com.bnbillains.services;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Reserva;
import com.bnbillains.repositories.FacturaRepository;
import org.springframework.data.domain.Sort; // IMPORTANTE
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
        // Regla de negocio: Una reserva solo puede tener UNA factura
        // Verificamos si es nueva (id null) y si la reserva ya tiene factura
        if (factura.getId() == null && factura.getReserva() != null) {
            if (facturaRepository.existsByReserva_Id(factura.getReserva().getId())) {
                throw new IllegalArgumentException("Esta reserva ya ha sido facturada. ¡No seas avaricioso!");
            }
        }
        return facturaRepository.save(factura);
    }

    public Factura actualizar(Long id, Factura factura) {
        return facturaRepository.findById(id)
                .map(f -> {
                    f.setFechaEmision(factura.getFechaEmision());
                    f.setImporte(factura.getImporte());
                    f.setImpuestosMalignos(factura.getImpuestosMalignos());
                    f.setMetodoPago(factura.getMetodoPago());
                    f.setReserva(factura.getReserva());
                    return facturaRepository.save(f);
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