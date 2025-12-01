package com.bnbillains.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * La clase `Factura` representa una entidad que modela el documento de cobro asociado
 * a una reserva dentro de la base de datos.
 * Contiene campos como `id`, `fechaEmision`, `importe`, `impuestosMalignos` y `metodoPago`.
 *
 * Las anotaciones de Lombok ayudan a reducir el código repetitivo al generar
 * automáticamente métodos comunes como getters, setters, constructores y toString.
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "factura") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    // Campo que almacena el identificador único de la factura.
    // Es una clave primaria autogenerada por la base de datos.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena la fecha en la que se emitió la factura.
    @NotNull(message = "{msg.factura.fecha.notNull}")
    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    // Campo que almacena el importe base de la reserva.
    @NotNull(message = "{msg.factura.importe.notNull}")
    @Positive(message = "{msg.factura.importe.positive}")
    @Column(name = "importe", nullable = false)
    private Double importe;

    // Campo para los impuestos adicionales (IVA del mal).
    @NotNull(message = "{msg.factura.impuestos.notNull}")
    @Min(value = 0, message = "{msg.factura.impuestos.min}")
    @Column(name = "impuestos_malignos", nullable = false)
    private Double impuestosMalignos;

    // Campo que describe cómo se pagó (Oro, Bitcoin, Almas...).
    @Column(name = "metodo_pago")
    private String metodoPago;

    // Relación Uno a Uno con la entidad Reserva.
    // Una factura pertenece a una única reserva.
    @OneToOne
    @JoinColumn(name = "reserva_id", unique = true)
    private Reserva reserva;

    /**
     * Este es un constructor personalizado que no incluye el campo `id`.
     * Se utiliza para crear instancias de `Factura` cuando no es necesario o no
     * se conoce el `id` (por ejemplo, antes de insertar en la base de datos).
     *
     * @param fechaEmision      Fecha de cobro.
     * @param importe           Cantidad a cobrar.
     * @param impuestosMalignos Tasas adicionales.
     * @param metodoPago        Forma de pago.
     * @param reserva           Reserva asociada.
     */
    public Factura(LocalDate fechaEmision, Double importe, Double impuestosMalignos, String metodoPago, Reserva reserva) {
        this.fechaEmision = fechaEmision;
        this.importe = importe;
        this.impuestosMalignos = impuestosMalignos;
        this.metodoPago = metodoPago;
        this.reserva = reserva;
    }
}
