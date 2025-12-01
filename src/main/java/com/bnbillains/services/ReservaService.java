package com.bnbillains.services;

import com.bnbillains.entities.Reserva;
import com.bnbillains.repositories.ReservaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;

    public ReservaService(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    public List<Reserva> obtenerTodas(Sort sort) {
        return reservaRepository.findAll(sort);
    }

    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva guardar(Reserva reserva) {
        validarFechas(reserva);
        // Aquí podríamos calcular el costeTotal automáticamente
        // long dias = ChronoUnit.DAYS.between(reserva.getFechaInicio(), reserva.getFechaFin());
        // reserva.setCosteTotal(dias * reserva.getGuarida().getPrecioNoche());

        return reservaRepository.save(reserva);
    }

    public Reserva actualizar(Long id, Reserva reserva) {
        validarFechas(reserva);
        return reservaRepository.findById(id)
                .map(r -> {
                    r.setFechaInicio(reserva.getFechaInicio());
                    r.setFechaFin(reserva.getFechaFin());
                    r.setCosteTotal(reserva.getCosteTotal());
                    r.setEstado(reserva.getEstado());
                    r.setVillano(reserva.getVillano());
                    r.setGuarida(reserva.getGuarida());
                    return reservaRepository.save(r);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> obtenerReservasPorVillano(Long villanoId) {
        return reservaRepository.findByVillano_Id(villanoId);
    }

    public List<Reserva> buscarPorGuarida(Long guaridaId) {
        return reservaRepository.findByGuarida_Id(guaridaId);
    }

    public List<Reserva> buscarPorEstado(Boolean estado, Sort sort) {
        return reservaRepository.findByEstado(estado, sort);
    }

    //validacion de fechas
    private void validarFechas(Reserva reserva) {
        if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null) {
            if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
            }
        }
    }
}