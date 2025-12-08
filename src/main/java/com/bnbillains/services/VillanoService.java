package com.bnbillains.services;

import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.VillanoRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VillanoService {

    private final VillanoRepository villanoRepository;

    public VillanoService(VillanoRepository villanoRepository) {
        this.villanoRepository = villanoRepository;
    }

    // --- MÉTODOS DE LECTURA ---

    // Obtiene todos los villanos permitiendo ordenación (ej: por Alias A-Z)
    public List<Villano> obtenerTodas(Sort sort) {
        return villanoRepository.findAll(sort);
    }

    public Optional<Villano> obtenerPorId(Long id) {
        return villanoRepository.findById(id);
    }

    // --- ESCRITURA Y EDICIÓN ---

    public Villano guardar(Villano villano) {
        return villanoRepository.save(villano);
    }

    public Villano actualizar(Long id, Villano villano) {
        return villanoRepository.findById(id)
                .map(v -> {
                    // Actualizamos campo a campo para mayor control
                    v.setNombre(villano.getNombre());
                    v.setAlias(villano.getAlias());
                    v.setCarnetDeVillano(villano.getCarnetDeVillano());
                    v.setEmail(villano.getEmail());
                    return villanoRepository.save(v);
                })
                .orElseThrow(() -> new IllegalArgumentException("Villano no encontrado"));
    }

    public void eliminar(Long id) {
        villanoRepository.deleteById(id);
    }

    // --- BÚSQUEDAS AVANZADAS ---

    // Búsqueda inteligente: El usuario escribe texto y buscamos coincidencia
    // tanto en el nombre real como en el alias criminal.
    public List<Villano> buscarFlexible(String texto, Sort sort) {
        return villanoRepository.findByNombreContainingIgnoreCaseOrAliasContainingIgnoreCase(texto, texto, sort);
    }

    // --- VALIDACIONES DE NEGOCIO ---

    // Comprobaciones para evitar duplicados antes de guardar
    public boolean existePorCarnetDeVillano(String carnet) {
        return villanoRepository.existsByCarnetDeVillano(carnet);
    }

    public boolean existePorEmail(String email) {
        return villanoRepository.existsByEmail(email);
    }
}