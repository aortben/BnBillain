package com.bnbillains.controllers;

import com.bnbillains.entities.SalaSecreta;
import com.bnbillains.services.SalaSecretaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;


import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class SalaSecretaController {

    private static final Logger logger = LoggerFactory.getLogger(SalaSecretaController.class);

    private final SalaSecretaService salaSecretaService;

    public SalaSecretaController(SalaSecretaService salaSecretaService) {
        this.salaSecretaService = salaSecretaService;
    }


    /**
     * Listado de Salas Secretas con Paginación Manual, Filtro y Ordenación.
     */
    @GetMapping("/salas_secretas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String search,
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Listando salas secretas... Pag: {}, Search: {}, Sort: {}", page, search, sort);

        Sort sortObj = getSort(sort);
        List<SalaSecreta> resultados;

        // Obtención de datos
        if (search != null && !search.isBlank()) {
            // Buscamos por la función principal (ej: "Laboratorio")
            resultados = salaSecretaService.buscarFlexible(search, sortObj);
        } else {
            resultados = salaSecretaService.obtenerTodasOrdenadas(sortObj);
        }

        // Paginación Manual
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<SalaSecreta> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // Modelo para la vista
        model.addAttribute("salas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "entities-html/sala-secreta"; // Ruta: templates/entities-html/sala-secreta.html
    }

    @GetMapping("/salas-secretas/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Formulario nueva sala secreta.");
        model.addAttribute("salaSecreta", new SalaSecreta());
        return "forms-html/sala-secreta-form"; // Ruta: templates/forms-html/sala-secreta-form.html
    }

    @GetMapping("/salas-secretas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando sala secreta ID {}", id);
        Optional<SalaSecreta> sala = salaSecretaService.obtenerPorId(id);
        if (sala.isPresent()) {
            model.addAttribute("salaSecreta", sala.get());
            return "forms-html/sala-secreta-form";
        }
        return "redirect:/salas-secretas";
    }

    @PostMapping("/salas-secretas/save")
    public String guardar(@Valid @ModelAttribute SalaSecreta salaSecreta,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forms-html/sala-secreta-form";
        }

        // Validación: Código de acceso único
        if (salaSecreta.getId() == null && salaSecretaService.existeCodigoAcceso(salaSecreta.getCodigoAcceso())) {
            logger.warn("Intento de duplicado código: {}", salaSecreta.getCodigoAcceso());
            redirectAttributes.addFlashAttribute("errorMessage", "El código de acceso ya está en uso.");
            return "redirect:/salas-secretas/new";
        }

        salaSecretaService.guardar(salaSecreta);
        logger.info("Sala secreta guardada: {}", salaSecreta.getCodigoAcceso());
        redirectAttributes.addFlashAttribute("successMessage", "Sala secreta registrada.");
        return "redirect:/salas-secretas";
    }

    @PostMapping("/salas-secretas/update")
    public String actualizar(@Valid @ModelAttribute SalaSecreta salaSecreta,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forms-html/sala-secreta-form";
        }

        try {
            salaSecretaService.actualizar(salaSecreta.getId(), salaSecreta);
            redirectAttributes.addFlashAttribute("successMessage", "Sala secreta actualizada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar.");
        }
        return "redirect:/salas-secretas";
    }

    @GetMapping("/salas-secretas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Eliminando sala secreta ID {}", id);
        try {
            salaSecretaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Sala secreta destruida.");
        } catch (Exception e) {
            // Seguramente falle si la sala está asignada a una Guarida (Integridad referencial)
            logger.error("Error al eliminar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar: Está vinculada a una guarida.");
        }
        return "redirect:/salas-secretas";
    }

    // Helper de ordenación
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "codeAsc" -> Sort.by("codigoAcceso").ascending();
            case "codeDesc" -> Sort.by("codigoAcceso").descending();
            case "funcAsc" -> Sort.by("funcionPrincipal").ascending();
            case "funcDesc" -> Sort.by("funcionPrincipal").descending();
            default -> Sort.by("id").ascending();
        };
    }

        //ver api rest
}