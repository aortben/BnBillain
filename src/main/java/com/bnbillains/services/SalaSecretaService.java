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
    private final GuaridaRepository guaridaRepository; // Necesario para romper el vínculo

    public SalaSecretaService(SalaSecretaRepository salaSecretaRepository, GuaridaRepository guaridaRepository) {
        this.salaSecretaRepository = salaSecretaRepository;
        this.guaridaRepository = guaridaRepository;
    }

    public List<SalaSecreta> obtenerTodas() {
        return salaSecretaRepository.findAll();
    }

    public List<SalaSecreta> obtenerTodasOrdenadas(Sort sort) {
        return salaSecretaRepository.findAll(sort);
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

    @Transactional // Asegura que la desvinculación y el borrado sean atómicos
    public void eliminar(Long id) {
        // 1. Verificar existencia
        SalaSecreta sala = salaSecretaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sala Secreta no encontrada con ID: " + id));

        // 2. Buscar si hay una Guarida vinculada a esta sala
        // (Esto requiere que GuaridaRepository tenga el método findBySalaSecreta)
        Optional<Guarida> guaridaAsociada = guaridaRepository.findBySalaSecreta(sala);

        // 3. Romper el vínculo si existe
        if (guaridaAsociada.isPresent()) {
            Guarida g = guaridaAsociada.get();
            g.setSalaSecreta(null); // Liberamos la sala
            guaridaRepository.save(g); // Actualizamos la guarida
        }

        // 4. Ahora sí, borrar la sala sin violar FK
        salaSecretaRepository.deleteById(id);
    }

    public Optional<SalaSecreta> obtenerPorCodigoAcceso(String codigo) {
        return salaSecretaRepository.findByCodigoAcceso(codigo);
    }

    // intento de busqueda flexible
    public List<SalaSecreta> buscarFlexible(String texto, Sort sort) {
        // Buscamos por función principal, que es lo más lógico para filtrar
        return salaSecretaRepository.findByFuncionPrincipalContainingIgnoreCase(texto, sort);
    }

    public boolean existeCodigoAcceso(String codigo) {
        return salaSecretaRepository.existsByCodigoAcceso(codigo);
    }
}