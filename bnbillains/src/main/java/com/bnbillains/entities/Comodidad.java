package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "comodidad")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comodidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre de la comodidad no puede estar vacío")
    @Column(name = "nombre", nullable = false, unique = true)
    private String nombre;

    @Column(name = "auto_destruccion")
    private Boolean autoDestruccion;

    // RELACIÓN N:M (Lado inverso)
    // mappedBy = "comodidades" hace referencia a la lista en la clase Guarida
    @ManyToMany(mappedBy = "comodidades")
    private List<Guarida> guaridas;
}