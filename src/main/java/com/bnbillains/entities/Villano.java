package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*; // Importamos todo para las exclusiones

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
    @Size(max = 255, message = "{msg.villano.name.size}")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    @NotEmpty(message = "{msg.villano.alias.notEmpty}")
    @Size(max = 255, message = "{msg.villano.alias.size}")
    @Column(name = "alias", nullable = false)
    private String alias;

    @NotEmpty(message = "{msg.villano.carnet.notEmpty}")
    @Size(max = 20, message = "{msg.villano.carnet.size}")
    @Pattern(
            regexp = "^[0-9]{3}[A-Za-z][0-9]{6}$",
            message = "El carnet debe ser 3 números, 1 letra y 6 números. Ejemplo: 123A123456"
    )
    @Column(name = "carne_villano", nullable = false, length = 20, unique = true)
    private String carnetDeVillano;

    @NotEmpty(message = "{msg.villano.email.notEmpty}")
    @Size(max = 255, message = "{msg.villano.email.size}")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // --- RELACIONES SEGURAS (Nombres tuyos + Protección nueva) ---

    @OneToMany(mappedBy = "villano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude          // Evita bucle infinito al imprimir en consola
    @EqualsAndHashCode.Exclude // Evita bucle infinito al comparar objetos
    private List<Reserva> reservasRealizadas;

    @OneToMany(mappedBy = "villano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude          // Evita bucle infinito al imprimir en consola
    @EqualsAndHashCode.Exclude // Evita bucle infinito al comparar objetos
    private List<Resena> resenasEscritas;

    // Constructor personalizado
    public Villano(String nombre, String alias, String carnetDeVillano, String email) {
        this.nombre = nombre;
        this.alias = alias;
        this.carnetDeVillano = carnetDeVillano;
        this.email = email;
    }
}