package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
import com.bnbillains.entities.Resena;
import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.ResenaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;

    public ResenaService(ResenaRepository resenaRepository) {
        this.resenaRepository = resenaRepository;
    }

    public List<Resena> obtenerTodas() {
        return resenaRepository.findAll();
    }

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
    
    public List<Resena> buscarPorPuntuacion(Long puntuacion) {
        return resenaRepository.findByPuntuacion(puntuacion);
    }
    
    public List<Resena> buscarPorGuarida(Guarida guarida) {
        return resenaRepository.findByGuarida(guarida);
    }
    public List<Resena> buscarPorVillano(Villano villano) {
        return resenaRepository.findByVillano(villano);
    }
    public List<Resena> buscarPorComentario(String texto) {
        return resenaRepository.findByComentarioContainingIgnoreCase(texto);
    }

    public List<Resena> buscarPorPuntuacionEntre(Long min, Long max) {
        return resenaRepository.findByPuntuacionBetween(min, max);
    }
    public List<Resena> obtenerTodosOrdenados(Sort sort) {

        return resenaRepository.findAll(sort);
    }

    public boolean existePorId(Long id) {

        return resenaRepository.existsById(id);
    }
    public List<Resena> obtenerResenasPorVillano(Long villanoId) {
        return resenaRepository.findByVillano_Id(villanoId);
    }
}
