package com.bnbillains.repositories;

import com.bnbillains.entities.SalaSecreta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaSecretaRepository extends JpaRepository<SalaSecreta, Long> {

    Optional<SalaSecreta> findByCodigoAcceso(String codigoAcceso);

    boolean existsByCodigoAcceso(String codigoAcceso);

    // Buscar por función principal ignorando mayúsculas y soportando ordenación
    List<SalaSecreta> findByFuncionPrincipalContainingIgnoreCase(String texto, Sort sort);

    // Buscar por código parcial ignorando mayúsculas y soportando ordenación
    List<SalaSecreta> findByCodigoAccesoContainingIgnoreCase(String texto, Sort sort);
}
