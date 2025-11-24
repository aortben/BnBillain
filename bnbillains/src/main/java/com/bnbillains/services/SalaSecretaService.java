package com.bnbillains.services;

import com.bnbillains.entities.SalaSecreta;
import com.bnbillains.repositories.SalaSecretaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SalaSecretaService {

    private final SalaSecretaRepository salaSecretaRepository;

    public SalaSecretaService(SalaSecretaRepository salaSecretaRepository) {
        this.salaSecretaRepository = salaSecretaRepository;
    }

    public List<SalaSecreta> obtenerTodas() {
        return salaSecretaRepository.findAll();
    }

    public Optional<SalaSecreta> obtenerPorId(Long id) {
        return salaSecretaRepository.findById(id);
    }

    public SalaSecreta guardar(SalaSecreta salaSecreta) {
        return salaSecretaRepository.save(salaSecreta);
    }

    public SalaSecreta actualizar(Long id, SalaSecreta salaSecreta) {
        return salaSecretaRepository.findById(id)
                .map(s -> {
                    s.setCodigoAcceso(salaSecreta.getCodigoAcceso());
                    s.setFuncionPrincipal(salaSecreta.getFuncionPrincipal());
                    s.setSalidaEmergencia(salaSecreta.getSalidaEmergencia());
                    return salaSecretaRepository.save(s);
                })
                .orElseThrow(() -> new IllegalArgumentException("Sala Secreta no encontrada"));
    }

    public void eliminar(Long id) {
        salaSecretaRepository.deleteById(id);
    }

    public Optional<SalaSecreta> obtenerPorCodigoAcceso(String codigo) {
        return salaSecretaRepository.findByCodigoAcceso(codigo);
    }

    public boolean existeCodigoAcceso(String codigo) {
        return salaSecretaRepository.existsByCodigoAcceso(codigo);
    }
}

