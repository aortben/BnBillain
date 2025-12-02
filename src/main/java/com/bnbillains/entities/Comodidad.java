package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
    @ManyToMany(mappedBy = "comodidades")
    //Gracias a esta anotación se puede borrar aunque la entidad pertenezca a una tabla
    @org.hibernate.annotations.OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE)
    private List<Guarida> guaridas = new ArrayList<>();

    public Comodidad(String nombre, Boolean autoDestruccion) {
        this.nombre = nombre;
        this.autoDestruccion = autoDestruccion;
    }
}

