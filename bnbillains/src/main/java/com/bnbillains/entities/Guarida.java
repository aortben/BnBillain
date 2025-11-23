package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Min(value = 1, message = "El precio debe ser positivo")
    @Column(name = "precio_noche", nullable = false)
    private Double precioNoche;

    @Column(name = "imagen")
    private String imagen;

    // --- RELACIÓN 1:1 ---
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "sala_secreta_id", referencedColumnName = "id", unique = true)
    private SalaSecreta salaSecreta;

    // --- RELACIÓN N:M ---
    @ManyToMany
    @JoinTable(
            name = "guaridaComodidades", // CORREGIDO: Coincide con tu SQL 'CREATE TABLE guaridaComodidades'
            joinColumns = @JoinColumn(name = "guarida_id"),
            inverseJoinColumns = @JoinColumn(name = "comodidades_id")
    )
    private List<Comodidad> comodidades = new ArrayList<>();


    @OneToMany(mappedBy = "guarida", cascade = CascadeType.ALL)
    private List<Resena> resenas; // Nota: en SQL la tabla es 'resena'

    @OneToMany(mappedBy = "guarida")
    private List<Reserva> reservas;

}