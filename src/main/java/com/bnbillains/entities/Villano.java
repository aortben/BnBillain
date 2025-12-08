package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

/**
 * Entidad que representa al cliente (usuario) de la plataforma.
 * Contiene la información de perfil y el historial de actividad.
 */
@Entity
@Table(name = "villano")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Villano {

    /**
     * Identificador único autogenerado (Clave Primaria).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre real del villano. Obligatorio.
     */
    @NotEmpty(message = "{msg.villano.name.notEmpty}")
    @Size(max = 255, message = "{msg.villano.name.size}")
    @Column(name = "nombre", nullable = false)
    private String nombre;

    /**
     * Nombre en clave o alias criminal. Obligatorio.
     */
    @NotEmpty(message = "{msg.villano.alias.notEmpty}")
    @Size(max = 255, message = "{msg.villano.alias.size}")
    @Column(name = "alias", nullable = false)
    private String alias;

    /**
     * Identificación oficial del gremio de villanos.
     * Debe ser único en el sistema.
     * Validación Regex estricta: 3 números + 1 Letra + 6 números.
     */
    @NotEmpty(message = "{msg.villano.carnet.notEmpty}")
    @Size(max = 20, message = "{msg.villano.carnet.size}")
    @Pattern(
            regexp = "^[0-9]{3}[A-Za-z][0-9]{6}$",
            message = "El carnet debe ser 3 números, 1 letra y 6 números. Ejemplo: 123A123456"
    )
    @Column(name = "carne_villano", nullable = false, length = 20, unique = true)
    private String carnetDeVillano;

    /**
     * Correo electrónico de contacto. Debe ser único.
     */
    @NotEmpty(message = "{msg.villano.email.notEmpty}")
    @Size(max = 255, message = "{msg.villano.email.size}")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    // --- RELACIONES ---

    /**
     * Historial de reservas realizadas por este villano.
     * mappedBy = "villano": La entidad Reserva es dueña de la relación.
     * FetchType.LAZY: Carga diferida para no saturar la memoria.
     * CascadeType.ALL: Si se borra el villano, se borran sus reservas.
     */
    @OneToMany(mappedBy = "villano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude          // Evita bucle infinito (StackOverflow) en logs
    @EqualsAndHashCode.Exclude // Evita bucle infinito en comparaciones
    private List<Reserva> reservasRealizadas;

    /**
     * Reseñas y opiniones escritas por el villano.
     */
    @OneToMany(mappedBy = "villano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Resena> resenasEscritas;

    /**
     * Constructor personalizado para registro rápido (sin ID ni listas).
     */
    public Villano(String nombre, String alias, String carnetDeVillano, String email) {
        this.nombre = nombre;
        this.alias = alias;
        this.carnetDeVillano = carnetDeVillano;
        this.email = email;
    }
}