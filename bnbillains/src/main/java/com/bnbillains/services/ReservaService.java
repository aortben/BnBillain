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

    public List<Reserva> obtenerTodas() {
        return reservaRepository.findAll();
    }

    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }

    public Reserva guardar(Reserva reserva) {
        return reservaRepository.save(reserva);
    }

    public Reserva actualizar(Long id, Reserva reserva) {
        return reservaRepository.findById(id)
                .map(g -> {
                    g.setFechaInicio(reserva.getFechaInicio());
                    g.setFechaFin(reserva.getFechaFin());
                    g.setCosteTotal(reserva.getCosteTotal());
                    g.setEstado(reserva.getEstado());
                    g.setVillano(reserva.getVillano());
                    g.setGuarida(reserva.getGuarida());
                    return reservaRepository.save(g);


                    estado BOOLEAN DEFAULT FALSE,
                    villano_id BIGINT,
                    guarida_id BIGINT,
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> buscarPorFInicio(LocalDate fechaInicio) {
        return reservaRepository.findByFInicio(fechaInicio);
    }

    public List<Reserva> buscarPorFFin(LocalDate fechaFin) {
        return reservaRepository.findByFFin(fechaFin);
    }

    public List<Reserva> obtenerTodosOrdenados(Sort sort) {
        return reservaRepository.findAll(sort);
    }

    public List<Reserva> buscarPorCosteTotal(Double fechaFin) {
        return reservaRepository.findByCosteTotal(fechaFin);
    }

    public List<Reserva> buscarPorEstado(Boolean estado) {
        return reservaRepository.findByEstado(estado);
    }
}

