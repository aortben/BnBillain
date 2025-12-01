package com.bnbillains.services;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Reserva;
import com.bnbillains.repositories.FacturaRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<Factura> obtenerTodas() {
        return facturaRepository.findAll();
    }

    public Optional<Factura> obtenerPorId(Long id) {
        return facturaRepository.findById(id);
    }

    public Factura guardar(Factura factura) {
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

    public Optional<Factura> obtenerPorReserva(Reserva reserva) {
        return facturaRepository.findByReserva(reserva);
    }

    public Optional<Factura> obtenerPorReservaId(Long reservaId) {
        return facturaRepository.findByReserva_Id(reservaId);
    }

    public List<Factura> obtenerPorFechaEmision(LocalDate fecha) {
        return facturaRepository.findByFechaEmision(fecha);
    }

    public List<Factura> buscarPorMetodoPago(String metodo) {
        return facturaRepository.findByMetodoPagoContainingIgnoreCase(metodo);
    }

    public boolean existePorReservaId(Long reservaId) {
        return facturaRepository.existsByReserva_Id(reservaId);
    }

    public List<Factura> buscarPorRangoFecha(LocalDate inicio, LocalDate fin) {
        return facturaRepository.findByFechaEmisionBetween(inicio, fin);
    }
    public List<Factura> obtenerFacturasPorVillano(Long villanoId) {
        return facturaRepository.findByReserva_Villano_Id(villanoId);
    }
}
