package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
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

    @NotNull(message = "{msg.reserva.fechaInicio.notNull}")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @NotNull(message = "{msg.reserva.fechaFin.notNull}")
    @Future(message = "{msg.reserva.fechaFin.future}")
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @NotNull(message = "{msg.reserva.coste.notNull}")
    @Min(value = 0, message = "{msg.reserva.coste.min}")
    @Column(name = "coste_total", nullable = false)
    private Double costeTotal;

    @Column(name = "estado")
    private Boolean estado;

    // --- RELACIONES (Protección Total) ---

    @ManyToOne
    @JoinColumn(name = "villano_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <--- Evita que Reserva compruebe el hashCode de Villano
    private Villano villano;

    @ManyToOne
    @JoinColumn(name = "guarida_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <--- Evita bucle con Guarida
    private Guarida guarida;

    @OneToOne(mappedBy = "reserva", cascade = CascadeType.ALL)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude // <--- Evita bucle con Factura
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