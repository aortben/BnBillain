package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * Entidad que modela la opinión y valoración (feedback) de un usuario.
 * Vincula un Villano con una Guarida mediante una puntuación y un comentario.
 */
@Entity
@Table(name = "resena")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    /**
     * Identificador único de la reseña (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Texto explicativo de la experiencia.
     * Limitado a 1000 caracteres para evitar textos excesivamente largos.
     */
    @Size(max = 1000, message = "{msg.resena.comentario.size}")
    @Column(name = "comentario", length = 1000)
    private String comentario;

    /**
     * Valoración numérica (Estrellas).
     * Validaciones estrictas: Obligatorio, mínimo 1, máximo 5.
     */
    @NotNull(message = "{msg.resena.puntuacion.notNull}")
    @Min(value = 1, message = "{msg.resena.puntuacion.min}")
    @Max(value = 5, message = "{msg.resena.puntuacion.max}")
    @Column(name = "puntuacion", nullable = false)
    private Long puntuacion;

    /**
     * Fecha de emisión de la reseña.
     */
    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    // --- RELACIONES ---

    /**
     * Autor de la reseña (Villano).
     * Relación Muchos a Uno: Un villano puede escribir muchas reseñas.
     */
    @ManyToOne
    @JoinColumn(name = "villano_id")
    private Villano villano;

    /**
     * Alojamiento evaluado (Guarida).
     * Relación Muchos a Uno: Una guarida puede recibir muchas reseñas.
     */
    @ManyToOne
    @JoinColumn(name = "guarida_id")
    private Guarida guarida;

    /**
     * Constructor personalizado para crear reseñas sin ID.
     * @param comentario Texto de opinión.
     * @param puntuacion Valor numérico (1-5).
     * @param fechaPublicacion Fecha de creación.
     * @param villano Autor.
     * @param guarida Lugar.
     */
    public Resena(String comentario, Long puntuacion, LocalDate fechaPublicacion, Villano villano, Guarida guarida) {
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.fechaPublicacion = fechaPublicacion;
        this.villano = villano;
        this.guarida = guarida;
    }
}