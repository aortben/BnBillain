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
                    g.setNombre(reserva.getNombre());
                    g.setDescripcion(reserva.getDescripcion());
                    g.setUbicacion(reserva.getUbicacion());
                    g.setPrecioNoche(reserva.getPrecioNoche());
                    g.setImagen(reserva.getImagen());
                    g.setComodidades(reserva.getComodidades());
                    g.setSalaSecreta(reserva.getSalaSecreta());
                    return reservaRepository.save(g);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrado"));
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

