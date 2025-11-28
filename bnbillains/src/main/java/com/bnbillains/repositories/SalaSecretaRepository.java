package com.bnbillains.repositories;

import com.bnbillains.entities.SalaSecreta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalaSecretaRepository extends JpaRepository<SalaSecreta, Long> {

    Optional<SalaSecreta> findByCodigoAcceso(String codigoAcceso);

    boolean existsByCodigoAcceso(String codigoAcceso);
}
