package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa una habitación oculta o de seguridad vinculada a una Guarida.
 * Contiene información sensible sobre accesos y funcionalidades estratégicas.
 */
@Entity
@Table(name = "salaSecreta") // Define la tabla en base de datos
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaSecreta {

    /**
     * Identificador único autogenerado (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Código o contraseña para acceder a la sala.
     * Restricción estricta: Máximo 8 caracteres (coherencia con paneles físicos).
     */
    @NotEmpty(message = "El código de acceso es obligatorio")
    @Size(max = 8, message = "El código no puede superar los 8 caracteres")
    @Column(name = "codigo_acceso", nullable = false, length = 8)
    private String codigoAcceso;

    /**
     * Propósito principal de la sala (Ej: "Búnker", "Armería").
     */
    @NotEmpty(message = "La función principal es obligatoria")
    @Column(name = "funcion_principal", nullable = false)
    private String funcionPrincipal;

    /**
     * Indica si la sala dispone de vía de escape.
     */
    @Column(name = "salida_emergencia")
    private Boolean salidaEmergencia;

    /**
     * Constructor personalizado para instanciar la sala sin ID previo.
     * Utilizado habitualmente al crear una nueva Guarida.
     */
    public SalaSecreta(String codigoAcceso, String funcionPrincipal, Boolean salidaEmergencia) {
        this.codigoAcceso = codigoAcceso;
        this.funcionPrincipal = funcionPrincipal;
        this.salidaEmergencia = salidaEmergencia;
    }
}