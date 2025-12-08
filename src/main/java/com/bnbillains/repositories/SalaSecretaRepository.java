package com.bnbillains.repositories;

import com.bnbillains.entities.SalaSecreta;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalaSecretaRepository extends JpaRepository<SalaSecreta, Long> {

    // Recupera una sala por su contraseña exacta (útil para lógica interna)
    Optional<SalaSecreta> findByCodigoAcceso(String codigoAcceso);

    // Validación rápida: ¿Existe ya este código? (Para evitar duplicados al crear)
    boolean existsByCodigoAcceso(String codigoAcceso);

    // --- MÉTODOS PARA EL BUSCADOR WEB ---

    // Busca por la descripción de uso (ej: "Nuclear") y permite ordenar
    List<SalaSecreta> findByFuncionPrincipalContainingIgnoreCase(String texto, Sort sort);

    // Busca por fragmento del código (ej: "123") ignorando mayúsculas
    List<SalaSecreta> findByCodigoAccesoContainingIgnoreCase(String texto, Sort sort);
}