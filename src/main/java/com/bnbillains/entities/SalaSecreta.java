package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "salaSecreta") // CORREGIDO: Coincide con tu SQL 'CREATE TABLE salaSecreta'
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SalaSecreta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El código de acceso es obligatorio")
    @Size(max = 8, message = "El código no puede superar los 8 caracteres") // CORREGIDO: Ajustado a VARCHAR(8)
    @Column(name = "codigo_acceso", nullable = false, length = 8)
    private String codigoAcceso;

    @NotEmpty(message = "La función principal es obligatoria")
    @Column(name = "funcion_principal", nullable = false)
    private String funcionPrincipal;

    @Column(name = "salida_emergencia")
    private Boolean salidaEmergencia;

    public SalaSecreta(String codigoAcceso, String funcionPrincipal,Boolean salidaEmergencia) {
        this.codigoAcceso = codigoAcceso;
        this.funcionPrincipal = funcionPrincipal;
        this.salidaEmergencia = salidaEmergencia;
    }
}