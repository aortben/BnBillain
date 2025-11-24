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
                .map(g -> {
                    g.setNombre(villano.getNombre());
                    g.setAlias(villano.getAlias());
                    g.setCarnetDeVillano(villano.getCarnetDeVillano());
                    g.setEmail(villano.getEmail());
                    return villanoRepository.save(g);
                })
        }
                .orElseThrow(() -> new IllegalArgumentException("Villano no encontrado"));
    }

    public void eliminar(Long id) {
        villanoRepository.deleteById(id);
    }

    public List<Villano> buscarPorNombre(String nombre) {
        return villanoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Villano> buscarPorAlias(String alias) {
        return villanoRepository.findByAliasContainingIgnoreCase(alias);
    }

    public List<Villano> obtenerTodosOrdenados(Sort sort) {
        return villanoRepository.findAll(sort);
    }

    public boolean existePorNombre(String nombre) {
        return villanoRepository.existsByNombre(nombre);
    }
}

