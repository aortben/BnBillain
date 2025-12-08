package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
import com.bnbillains.entities.SalaSecreta;
import com.bnbillains.repositories.GuaridaRepository;
import com.bnbillains.repositories.SalaSecretaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SalaSecretaService {

    private final SalaSecretaRepository salaSecretaRepository;
    private final GuaridaRepository guaridaRepository;

    public SalaSecretaService(SalaSecretaRepository salaSecretaRepository, GuaridaRepository guaridaRepository) {
        this.salaSecretaRepository = salaSecretaRepository;
        this.guaridaRepository = guaridaRepository;
    }

    // Métodos estándar
    public List<SalaSecreta> obtenerTodasOrdenadas(Sort sort) { return salaSecretaRepository.findAll(sort); }
    public Optional<SalaSecreta> obtenerPorId(Long id) { return salaSecretaRepository.findById(id); }
    public SalaSecreta guardar(SalaSecreta s) { return salaSecretaRepository.save(s); }

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

    // --- BORRADO SEGURO (Gestión de Relaciones) ---
    @Transactional
    public void eliminar(Long id) {
        SalaSecreta sala = salaSecretaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sala no encontrada"));

        // Comprobamos si hay una guarida usando esta sala
        Optional<Guarida> guaridaAsociada = guaridaRepository.findBySalaSecreta(sala);

        // Si existe, rompemos el vínculo (la ponemos a null) antes de borrar la sala
        // para evitar errores de base de datos.
        if (guaridaAsociada.isPresent()) {
            Guarida g = guaridaAsociada.get();
            g.setSalaSecreta(null);
            guaridaRepository.save(g);
        }

        salaSecretaRepository.deleteById(id);
    }

    public List<SalaSecreta> buscarFlexible(String texto, Sort sort) {
        return salaSecretaRepository.findByFuncionPrincipalContainingIgnoreCase(texto, sort);
    }

    public boolean existeCodigoAcceso(String codigo) {
        return salaSecretaRepository.existsByCodigoAcceso(codigo);
    }
}