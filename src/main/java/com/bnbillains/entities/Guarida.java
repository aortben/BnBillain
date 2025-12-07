package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guarida")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Guarida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La guarida necesita un nombre imponente")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Size(max = 1000, message = "La descripción es demasiado larga")
    @Column(name = "descripcion", length = 1000)
    private String descripcion;

    @NotBlank(message = "La ubicación es obligatoria")
    @Column(name = "ubicacion", nullable = false)
    private String ubicacion;

    @DecimalMin(value = "1.0", message = "El precio debe ser positivo")
    @Column(name = "precio_noche", nullable = false)
    private Double precioNoche;

    @Column(name = "imagen")
    private String imagen;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sala_secreta_id", referencedColumnName = "id", unique = true)
    private SalaSecreta salaSecreta;

    // --- RELACIONES ---

    @ManyToMany
    @JoinTable(
            name = "guarida_comodidades",
            joinColumns = @JoinColumn(name = "guarida_id"),
            inverseJoinColumns = @JoinColumn(name = "comodidades_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Comodidad> comodidades = new ArrayList<>();

    @OneToMany(mappedBy = "guarida", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Resena> resenas = new ArrayList<>();

    // ✅ CORRECCIÓN DEL ERROR SQL AQUÍ:
    // Añadido cascade ALL y orphanRemoval. Al borrar la guarida, se borran las reservas.
    @OneToMany(mappedBy = "guarida", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Reserva> reservas = new ArrayList<>();

    // Constructor personalizado (sin ID ni listas)
    public Guarida(String nombre, String descripcion, String ubicacion, Double precioNoche, String imagen, SalaSecreta salaSecreta) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.ubicacion = ubicacion;
        this.precioNoche = precioNoche;
        this.imagen = imagen;
        this.salaSecreta = salaSecreta;
    }

    public String getPathImagen() {
        // Si es nulo o cadena vacía, devuelve la imagen por defecto
        if (this.imagen == null || this.imagen.trim().isEmpty()) {
            // Spring Boot "ignora" la parte de src/main/resources/static
            // y sirve lo de adentro directamente.
            return "/images/guarida-default.jpg";
        }
        // Si hay imagen, devuelve la ruta de subida configurada
        return "/uploads/" + this.imagen;
    }
}