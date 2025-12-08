package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

/**
 * Entidad que representa la transacción de alquiler.
 * Vincula un Villano con una Guarida durante un periodo específico y genera una Factura.
 */
@Entity
@Table(name = "reserva")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    /**
     * Identificador único de la reserva (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha de comienzo de la estancia.
     */
    @NotNull(message = "La fecha de inicio es obligatoria")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    /**
     * Fecha de finalización.
     * @Future: Valida que la fecha sea posterior al momento actual.
     */
    @NotNull(message = "La fecha de fin es obligatoria")
    @Future(message = "La fecha de fin debe ser futura")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    /**
     * Coste total calculado (Días * Precio/Noche).
     * No tiene @NotNull porque se calcula en el Servicio antes de guardar,
     * llegando como null desde el formulario inicial.
     */
    @Column(name = "coste_total")
    private Double costeTotal;

    /**
     * Estado de la reserva (Confirmada vs Pendiente).
     */
    @Column(name = "estado")
    private Boolean estado;

    // --- RELACIONES ---

    /**
     * El cliente que realiza la reserva.
     * Se excluye de ToString/Equals para evitar referencias circulares.
     */
    @ManyToOne
    @JoinColumn(name = "villano_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Villano villano;

    /**
     * La guarida objeto del alquiler.
     */
    @ManyToOne
    @JoinColumn(name = "guarida_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Guarida guarida;

    /**
     * Factura asociada a la transacción.
     * mappedBy = "reserva": La entidad Factura es la dueña de la relación.
     * CascadeType.ALL: Si se borra la reserva, se borra la factura.
     */
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Factura factura;

    /**
     * Constructor auxiliar para instanciación rápida.
     */
    public Reserva(LocalDate fechaInicio, LocalDate fechaFin, Double costeTotal, Boolean estado, Villano villano, Guarida guarida) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.costeTotal = costeTotal;
        this.estado = estado;
        this.villano = villano;
        this.guarida = guarida;
    }
}