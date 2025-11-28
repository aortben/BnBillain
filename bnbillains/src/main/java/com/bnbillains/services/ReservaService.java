package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Reserva;
import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.ReservaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
                .map(rv -> {
                    rv.setFechaInicio(reserva.getFechaInicio());
                    rv.setFechaFin(reserva.getFechaFin());
                    rv.setCosteTotal(reserva.getCosteTotal());
                    rv.setEstado(reserva.getEstado());
                    rv.setVillano(reserva.getVillano());
                    rv.setGuarida(reserva.getGuarida());
                    rv.setFactura(reserva.getFactura());
                    return reservaRepository.save(rv);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    public void eliminar(Long id) {
        reservaRepository.deleteById(id);
    }

    public List<Reserva> buscarPorFInicio(LocalDate fechaInicio) {
        return reservaRepository.findByFechaInicio(fechaInicio);
    }

    public List<Reserva> buscarFFinEntre(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findByFechaFinBetween(fechaInicio, fechaFin);
    }

    public List<Reserva> obtenerTodosOrdenados(Sort sort) {
        return reservaRepository.findAll(sort);
    }

    public List<Reserva> buscarFInicioEntre(LocalDate fechaInicio, LocalDate fechaFin) {
        return reservaRepository.findByFechaInicioBetween(fechaInicio, fechaFin);
    }
    public List<Reserva> obtenerReservasPorVillano(Long villano_id){
        return reservaRepository.findByVillanoId(villano_id);
    }
    public Optional<Reserva> buscarIdFactura(Long id) {
        return reservaRepository.findByFactura_Id(id);
    }
    public List<Reserva> buscarGuarida(Guarida guarida) {
        return reservaRepository.findByGuarida(guarida);
    }
    public List<Reserva> buscarVillano(Villano villano) {
        return reservaRepository.findByVillano(villano);
    }
    public List<Reserva> buscarPorEstado(Boolean estado) {
        return reservaRepository.findByEstado(estado);
    }
}