package com.bnbillains.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "villano")
public class Villano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String nombre;

    @NotBlank(message = "El alias no puede estar vacío")
    private String alias;

    @NotBlank(message = "El carnet de villano no puede estar vacío")
    @Pattern(
            regexp = "^[0-9]{3}[A-Za-z][0-9]{6}$",
            message = "El carnet debe ser 3 números, 1 letra y 6 números. Ejemplo: 123A123456"
    )
    private String carnetVillano;

    @NotBlank(message = "El email no puede estar vacío")
    @Email(message = "El email no es válido")
    private String email;

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getCarnetVillano() {
        return carnetVillano;
    }

    public void setCarnetVillano(String carnetVillano) {
        this.carnetVillano = carnetVillano;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

