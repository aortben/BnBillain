package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "villano")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Villano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.villano.name.notEmpty}")
    @Size(max = 150, message = "{msg.villano.name.size}")
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @NotEmpty(message = "{msg.villano.alias.notEmpty}")
    @Size(max = 150, message = "{msg.villano.alias.size}")
    @Column(name = "alias", nullable = false, length = 150)
    private String alias;

    @NotEmpty(message = "{msg.villano.carnet.notEmpty}")
    @Size(max = 20, message = "{msg.villano.carnet.size}")
    @Column(name = "carne_villano", nullable = false, length = 20)
    private String carnetDeVillano;

    @NotEmpty(message = "{msg.villano.email.notEmpty}")
    @Size(max = 150, message = "{msg.villano.email.size}")
    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @OneToMany(mappedBy = "villano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reserva> reservasRealizadas;

    @OneToMany(mappedBy = "villano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Resena> resenasEscritas;

    public Villano(String nombre, String alias, String carnetDeVillano, String email) {
        this.nombre = nombre;
        this.alias = alias;
        this.carnetDeVillano = carnetDeVillano;
        this.email = email;
    }
}
