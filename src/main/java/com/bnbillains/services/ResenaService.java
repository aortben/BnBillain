package com.bnbillains.services;

import com.bnbillains.entities.Resena;
import com.bnbillains.repositories.ResenaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public ResenaService(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    public List<Resena> obtenerTodas(Sort sort) { // Acepta sort
        return resenaRepository.findAll(sort);
    }

    // Legacy
    public List<Resena> obtenerTodas() { return resenaRepository.findAll(); }

    public Optional<Resena> obtenerPorId(Long id) {
        return resenaRepository.findById(id);
    }

    public Resena guardar(Resena resena) {
        return resenaRepository.save(resena);
    }

    public Resena actualizar(Long id, Resena resena) {
        return resenaRepository.findById(id)
                .map(rn -> {
                    rn.setComentario(resena.getComentario());
                    rn.setPuntuacion(resena.getPuntuacion());
                    rn.setFechaPublicacion(resena.getFechaPublicacion());
                    rn.setVillano(resena.getVillano());
                    rn.setGuarida(resena.getGuarida());
                    return resenaRepository.save(rn);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reseña no encontrada"));
    }

    public void eliminar(Long id) {
        resenaRepository.deleteById(id);
    }

    // --- BÚSQUEDAS PRO ---
    public List<Resena> buscarPorPuntuacion(Long puntuacion, Sort sort) {
        return resenaRepository.findByPuntuacion(puntuacion, sort);
    }

    public List<Resena> buscarPorComentario(String texto, Sort sort) {
        return resenaRepository.findByComentarioContainingIgnoreCase(texto, sort);
    }
}