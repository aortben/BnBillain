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

    // --- LECTURA ---
    public List<Villano> obtenerTodas(Sort sort) { // Acepta sort
        return villanoRepository.findAll(sort);
    }

    // Método para compatibilidad si alguien lo usa sin sort
    public List<Villano> obtenerTodas() {
        return villanoRepository.findAll();
    }

    public Optional<Villano> obtenerPorId(Long id) {
        return villanoRepository.findById(id);
    }

    // --- ESCRITURA ---
    public Villano guardar(Villano villano) {
        return villanoRepository.save(villano);
    }

    public Villano actualizar(Long id, Villano villano) {
        return villanoRepository.findById(id)
                .map(v -> {
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

    // --- BÚSQUEDA ---
    public Optional<Villano> buscarPorEmail(String email) {
        return villanoRepository.findByEmail(email);
    }

    public Optional<Villano> buscarPorCarnetDeVillano(String carnet) {
        return villanoRepository.findByCarnetDeVillano(carnet);
    }

    // Búsqueda inteligente (Nombre o Alias) con Orden
    public List<Villano> buscarFlexible(String texto, Sort sort) {
        return villanoRepository.findByNombreContainingIgnoreCaseOrAliasContainingIgnoreCase(texto, texto, sort);
    }

    // --- VALIDACIONES ---
    public boolean existePorCarnetDeVillano(String carnet) {
        return villanoRepository.existsByCarnetDeVillano(carnet);
    }

    public boolean existePorEmail(String email) {
        return villanoRepository.existsByEmail(email);
    }
}