package com.bnbillains.services;

import com.bnbillains.entities.Comodidad;
import com.bnbillains.repositories.ComodidadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComodidadService {

    private final ComodidadRepository comodidadRepository;

    public ComodidadService(ComodidadRepository comodidadRepository) {
        this.comodidadRepository = comodidadRepository;
    }

    public List<Comodidad> obtenerTodas() {
        return comodidadRepository.findAll();
    }

    public Optional<Comodidad> obtenerPorId(Long id) {
        return comodidadRepository.findById(id);
    }

    public Comodidad guardar(Comodidad comodidad) {
        return comodidadRepository.save(comodidad);
    }

    public Comodidad actualizar(Long id, Comodidad comodidad) {
        return comodidadRepository.findById(id)
                .map(c -> {
                    c.setNombre(comodidad.getNombre());
                    c.setAutoDestruccion(comodidad.getAutoDestruccion());
                    return comodidadRepository.save(c);
                })
                .orElseThrow(() -> new IllegalArgumentException("Comodidad no encontrada"));
    }

    public void eliminar(Long id) {
        comodidadRepository.deleteById(id);
    }

    public Comodidad obtenerPorNombre(String nombre) {
        return comodidadRepository.findByNombreIgnoreCase(nombre);
    }

    public List<Comodidad> buscarPorFragmento(String fragmento) {
        return comodidadRepository.findByNombreContainingIgnoreCase(fragmento);
    }

    public boolean existePorNombre(String nombre) {
        return comodidadRepository.existsByNombreIgnoreCase(nombre);
    }
}

