package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad central que representa el alojamiento o base disponible para alquiler.
 * Agrupa toda la información del producto, incluyendo precios, imágenes y relaciones.
 */
@Entity
@Table(name = "guarida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guarida {

    /**
     * Identificador único de la guarida (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre comercial de la guarida. Obligatorio.
     */
    @NotBlank(message = "La guarida necesita un nombre imponente")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /**
     * Descripción detallada. Limitada a 1000 caracteres para evitar textos excesivos.
     */
    @Size(max = 1000, message = "La descripción es demasiado larga")
    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    /**
     * Ubicación física o secreta de la guarida.
     */
    @NotBlank(message = "La ubicación es obligatoria")
    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    /**
     * Precio por noche. Debe ser un valor positivo (mínimo 1.0).
     */
    @DecimalMin(value = "1.0", message = "El precio debe ser positivo")
    @Column(name = "precio_noche", nullable = false)
    private Double precioNoche;

    /**
     * Nombre del archivo de imagen (ej: "volcan.jpg").
     * Puede ser nulo si no se sube imagen.
     */
    @Column(name = "imagen")
    private String imagen;

    /**
     * Relación 1:1 con la Sala Secreta.
     * CascadeType.ALL: El ciclo de vida de la sala depende de la guarida.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sala_secreta_id", referencedColumnName = "id", unique = true)
    private SalaSecreta salaSecreta;

    // --- RELACIONES ---

    /**
     * Relación N:M con Comodidades.
     * Gestionada mediante la tabla intermedia 'guarida_comodidades'.
     */
    @ManyToMany
    @JoinTable(
            name = "guarida_comodidades",
            joinColumns = @JoinColumn(name = "guarida_id"),
            inverseJoinColumns = @JoinColumn(name = "comodidades_id")
    )
    @ToString.Exclude // Evita bucles al imprimir
    @EqualsAndHashCode.Exclude
    private List<Comodidad> comodidades = new ArrayList<>();

    /**
     * Listado de reseñas recibidas.
     * orphanRemoval = true: Si se borra la guarida, se borran sus reseñas.
     */
    @OneToMany(mappedBy = "guarida", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Resena> resenas = new ArrayList<>();

    /**
     * Historial de reservas.
     * orphanRemoval = true: Garantiza que no queden reservas "zombis" si la guarida desaparece.
     */
    @OneToMany(mappedBy = "guarida", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas = new ArrayList<>();

    /**
     * Constructor auxiliar para crear guaridas sin listas iniciales.
     */
    public Guarida(String nombre, String descripcion, String ubicacion, Double precioNoche, String imagen, SalaSecreta salaSecreta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.precioNoche = precioNoche;
        this.imagen = imagen;
        this.salaSecreta = salaSecreta;
    }

    /**
     * Método de utilidad para la vista (Thymeleaf).
     * Devuelve la ruta relativa de la imagen o una por defecto si no existe.
     * @return Ruta string para el atributo 'src' de la etiqueta img.
     */
    public String getPathImagen() {
        if (this.imagen == null || this.imagen.trim().isEmpty()) {
            return "/images/guarida-default.jpg";
        }
        return "/uploads/" + this.imagen;
    }
}