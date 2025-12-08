package com.bnbillains.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador raíz de la aplicación.
 * Gestiona la página de bienvenida (Landing Page).
 */
@Controller
public class HomeController {

    /**
     * Maneja la petición a la raíz del sitio ("/")
     * @return La vista "index" (templates/index.html)
     */
    @GetMapping("/")
    public String home(Model model) {
        // Aquí se podrían añadir atributos de bienvenida si fuera necesario
        return "index";
    }
}