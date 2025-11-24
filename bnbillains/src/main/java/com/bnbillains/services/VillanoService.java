package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
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

    public List<Villano> obtenerTodas() {
        return villanoRepository.findAll();
    }

    public Optional<Villano> obtenerPorId(Long id) {
        return villanoRepository.findById(id);
    }

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
                .orElseThrow(() -> new IllegalArgumentException("Guarida no encontrada"));
    }

    public void eliminar(Long id) {
        villanoRepository.deleteById(id);
    }

    public Optional<Villano> buscarPorEmail(String email) {
        return villanoRepository.findByEmail(email);
    }

    public Optional<Villano> buscarPorCarnetDeVillano(String carnetDeVillano) {
        return villanoRepository.findByCarnetDeVillano(carnetDeVillano);
    }

    public List<Villano> obtenerTodosOrdenados(Sort sort) {

        return villanoRepository.findAll(sort);
    }

    public boolean existePorCarnetDeVillano(String carnetDeVillano) {

        return villanoRepository.existsByCarnetDeVillano(carnetDeVillano);
    }

    public boolean existePorEmail(String email) {

        return villanoRepository.existsByEmail(email);
    }
}
