package com.bnbillains.entities;

import jakarta.persistence.*; // Anotaciones de JPA
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * La clase `Reserva` representa una entidad que modela la transacción de alquiler
 * de una guarida por parte de un villano.
 * Es la entidad central que conecta `Villano`, `Guarida` y `Factura`.
 */
@Entity // Marca esta clase como una entidad gestionada por JPA.
@Table(name = "reserva") // Especifica el nombre de la tabla asociada a esta entidad.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {

    // Campo que almacena el identificador único de la reserva.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo para la fecha de entrada. Debe ser futura o presente.
    @NotNull(message = "{msg.reserva.fechaInicio.notNull}")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    // Campo para la fecha de salida. Debe ser futura.
    @NotNull(message = "{msg.reserva.fechaFin.notNull}")
    @Future(message = "{msg.reserva.fechaFin.future}")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    // Campo calculado del coste total de la estancia.
    @NotNull(message = "{msg.reserva.coste.notNull}")
    @Min(value = 0, message = "{msg.reserva.coste.min}")
    @Column(name = "coste_total", nullable = false)
    private Double costeTotal;

    // Estado de la reserva (true = confirmada, false = pendiente/cancelada).
    @Column(name = "estado")
    private Boolean estado;

    // Relación Muchos a Uno con Villano (Quien reserva).
    @ManyToOne
    @JoinColumn(name = "villano_id")
    private Villano villano;

    // Relación Muchos a Uno con Guarida (Qué se reserva).
    @ManyToOne
    @JoinColumn(name = "guarida_id")
    private Guarida guarida;

    // Relación Uno a Uno con Factura (El cobro generado).
    // mappedBy indica que la clave foránea está en la clase Factura.
    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    private Factura factura;

    /**
     * Constructor personalizado sin `id` y sin `factura` (se genera después).
     *
     * @param fechaInicio Fecha de entrada.
     * @param fechaFin Fecha de salida.
     * @param costeTotal Precio final.
     * @param estado Estado de la reserva.
     * @param villano Usuario cliente.
     * @param guarida Producto alquilado.
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