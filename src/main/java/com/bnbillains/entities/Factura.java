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

    @NotNull(message = "{msg.factura.fecha.notNull}")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @NotNull(message = "{msg.factura.importe.notNull}")
    @Positive(message = "{msg.factura.importe.positive}")
    @Column(name = "importe", nullable = false)
    private Double importe;

    @NotNull(message = "{msg.factura.impuestos.notNull}")
    @Min(value = 0, message = "{msg.factura.impuestos.min}")
    @Column(name = "impuestos_malignos", nullable = false)
    private Double impuestosMalignos;

    @Column(name = "metodo_pago")
    private String metodoPago;

    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <--- ROMPE EL CICLO FINAL
    private Reserva reserva;

    public Factura(LocalDate fechaEmision, Double importe, Double impuestosMalignos, String metodoPago, Reserva reserva) {
        this.fechaEmision = fechaEmision;
        this.importe = importe;
        this.impuestosMalignos = impuestosMalignos;
        this.metodoPago = metodoPago;
        this.reserva = reserva;
    }
}