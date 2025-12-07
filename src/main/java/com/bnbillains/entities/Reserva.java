package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*; // Importamos todo Lombok
import java.time.LocalDate;

@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // --- CORRECCIÓN 1: QUITADO @NotNull ---
    // Este campo se calcula en el servicio, así que entra como null al principio.
    @Column(name = "coste_total")
    private Double costeTotal;

    @Column(name = "estado")
    private Boolean estado;

    // --- CORRECCIÓN 2: AÑADIDOS EXCLUDES ---
    @ManyToOne
    @JoinColumn(name = "villano_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Villano villano;

    @ManyToOne
    @JoinColumn(name = "guarida_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Guarida guarida;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Factura factura;

    public Reserva(LocalDate fechaInicio, LocalDate fechaFin, Double costeTotal, Boolean estado, Villano villano, Guarida guarida) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costeTotal = costeTotal;
        this.estado = estado;
        this.villano = villano;
        this.guarida = guarida;
    }
}