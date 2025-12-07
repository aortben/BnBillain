package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "factura")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "La fecha es obligatoria")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "El importe es obligatorio")
    @Positive(message = "El importe debe ser positivo")
    @Column(name = "importe", nullable = false)
    private Double importe;

    @NotNull(message = "Los impuestos son obligatorios")
    @Min(value = 0, message = "Los impuestos no pueden ser negativos")
    @Column(name = "impuestos_malignos", nullable = false)
    private Double impuestosMalignos;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    @ToString.Exclude // <--- VITAL PARA EVITAR BUCLES
    @EqualsAndHashCode.Exclude
    private Reserva reserva;

    public Factura(LocalDate fechaEmision, Double importe, Double impuestosMalignos, String metodoPago, Reserva reserva) {
        this.fechaEmision = fechaEmision;
        this.importe = importe;
        this.impuestosMalignos = impuestosMalignos;
        this.metodoPago = metodoPago;
        this.reserva = reserva;
    }
}