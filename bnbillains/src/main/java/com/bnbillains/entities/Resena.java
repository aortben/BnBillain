package com.bnbillains.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * La clase `Reseña` representa una entidad que modela la opinión y valoración
 * que un villano deja sobre una guarida tras su estancia.
 * Contiene validaciones específicas para la puntuación (1-5 estrellas).
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "reseña") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Resena {

    // Campo que almacena el identificador único de la reseña.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo para el texto de la opinión.
    @Size(max = 1000, message = "{msg.resena.comentario.size}")
    @Column(name = "comentario", length = 1000)
    private String comentario;

    // Campo para la puntuación numérica (Estrellas).
    // Se valida que esté entre 1 y 5.
    @NotNull(message = "{msg.resena.puntuacion.notNull}")
    @Min(value = 1, message = "{msg.resena.puntuacion.min}")
    @Max(value = 5, message = "{msg.resena.puntuacion.max}")
    @Column(name = "puntuacion", nullable = false)
    private Long puntuacion;

    // Fecha en la que se publicó el comentario.
    @Column(name = "fecha_publicacion")
    private LocalDate fechaPublicacion;

    // Relación Muchos a Uno con Villano (Autor de la reseña).
    @ManyToOne
    @JoinColumn(name = "villano_id")
    private Villano villano;

    // Relación Muchos a Uno con Guarida (Destino de la reseña).
    @ManyToOne
    @JoinColumn(name = "guarida_id")
    private Guarida guarida;

    /**
     * Constructor personalizado sin `id`.
     *
     * @param comentario Texto de la opinión.
     * @param puntuacion Valoración numérica.
     * @param fechaPublicacion Fecha de creación.
     * @param villano Autor.
     * @param guarida Lugar valorado.
     */
    public Resena(String comentario, Long puntuacion, LocalDate fechaPublicacion, Villano villano, Guarida guarida) {
        this.comentario = comentario;
        this.puntuacion = puntuacion;
        this.fechaPublicacion = fechaPublicacion;
        this.villano = villano;
        this.guarida = guarida;
    }
}