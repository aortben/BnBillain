package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa una característica o servicio adicional (Amenity)
 * disponible en una o varias guaridas.
 */
@Entity
@Table(name = "comodidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comodidad {

    /**
     * Identificador único de la comodidad (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre descriptivo de la comodidad (ej: "Rayo Láser").
     * Debe ser único en el sistema para evitar duplicidades en el catálogo.
     */
    @NotEmpty(message = "El nombre de la comodidad no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    /**
     * Indica si la comodidad incluye características explosivas o de autodestrucción.
     */
    @Column(name = "auto_destruccion")
    private Boolean autoDestruccion;

    // --- RELACIÓN N:M (Lado inverso) ---

    /**
     * Lista de guaridas que poseen esta comodidad.
     * <p>
     * mappedBy = "comodidades": Indica que 'Guarida' es la propietaria de la relación.
     * @OnDelete(CASCADE): Instrucción para Hibernate/DB. Si borramos esta Comodidad,
     * se borran las filas correspondientes en la tabla intermedia 'guarida_comodidades',
     * evitando errores de Foreign Key.
     */
    @ManyToMany(mappedBy = "comodidades")
    @ToString.Exclude // Evita bucles infinitos al imprimir logs
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<Guarida> guaridas = new ArrayList<>();

    /**
     * Constructor personalizado para crear comodidades sin ID ni lista de guaridas inicial.
     * @param nombre Nombre de la comodidad.
     * @param autoDestruccion Si tiene capacidad de autodestrucción.
     */
    public Comodidad(String nombre, Boolean autoDestruccion) {
        this.nombre = nombre;
        this.autoDestruccion = autoDestruccion;
    }
}