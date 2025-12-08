package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

/**
 * Entidad que representa el documento de cobro asociado a una estancia.
 * Gestiona la información fiscal, los totales y la vinculación con la reserva.
 */
@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    /**
     * Identificador único de la factura (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha de emisión del documento.
     * Es obligatoria para la validez del registro contable.
     */
    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    /**
     * Importe base de la operación (sin impuestos).
     * Debe ser un valor positivo.
     */
    @NotNull(message = "El importe es obligatorio")
    @Positive(message = "El importe debe ser positivo")
    @Column(name = "importe", nullable = false)
    private Double importe;

    /**
     * Impuestos aplicados (IVA maligno).
     * Se permite valor 0, pero no negativos.
     */
    @NotNull(message = "Los impuestos son obligatorios")
    @Min(value = 0, message = "Los impuestos no pueden ser negativos")
    @Column(name = "impuestos_malignos", nullable = false)
    private Double impuestosMalignos;

    /**
     * Modalidad de pago utilizada (Efectivo, Cripto, etc.).
     */
    @Column(name = "metodo_pago")
    private String metodoPago;

    /**
     * Relación 1:1 con la Reserva asociada.
     * Esta entidad posee la Foreign Key ('reserva_id').
     * Se excluye de Lombok para evitar referencias circulares infinitas.
     */
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    @ToString.Exclude // VITAL: Rompe el bucle con Reserva.toString()
    @EqualsAndHashCode.Exclude
    private Reserva reserva;

    /**
     * Constructor para la creación de facturas antes de su persistencia (sin ID).
     */
    public Factura(LocalDate fechaEmision, Double importe, Double impuestosMalignos, String metodoPago, Reserva reserva) {
        this.fechaEmision = fechaEmision;
        this.importe = importe;
        this.impuestosMalignos = impuestosMalignos;
        this.metodoPago = metodoPago;
        this.reserva = reserva;
    }
}