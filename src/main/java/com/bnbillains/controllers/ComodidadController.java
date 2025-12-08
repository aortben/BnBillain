package com.bnbillains.controllers;

import com.bnbillains.entities.Comodidad;
import com.bnbillains.services.ComodidadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

// Importaciones para la API (Código desactivado intencionalmente)
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controlador encargado de gestionar las peticiones web para la entidad Comodidad.
 * Maneja el listado, creación, edición y eliminación mediante vistas Thymeleaf.
 */
@Controller
public class ComodidadController {

    private static final Logger logger = LoggerFactory.getLogger(ComodidadController.class);

    private final ComodidadService comodidadService;

    /**
     * Inyección de dependencias por constructor.
     */
    public ComodidadController(ComodidadService comodidadService) {
        this.comodidadService = comodidadService;
    }

    // --- SECCIÓN MVC (Vistas HTML) ---

    /**
     * Muestra el listado de comodidades con soporte para paginación, búsqueda y ordenación.
     * * @param page Número de página actual (por defecto 1).
     * @param search Texto para filtrar por nombre (opcional).
     * @param sort Criterio de ordenación (opcional).
     * @param model Modelo para pasar datos a la vista.
     * @return La vista "entities-html/comodidad".
     */
    @GetMapping("/comodidades")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String search,
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Listando comodidades... Pag: {}, Search: {}, Sort: {}", page, search, sort);

        Sort sortObj = getSort(sort);
        List<Comodidad> resultados;

        // Lógica de obtención de datos (Filtrados u ordenados)
        if (search != null && !search.isBlank()) {
            resultados = comodidadService.buscarPorFragmentoOrdenado(search, sortObj);
        } else {
            resultados = comodidadService.obtenerTodasOrdenadas(sortObj);
        }

        // Lógica de Paginación Manual en memoria
        int pageSize = 5; // Elementos por página
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<Comodidad> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // Pasar atributos a la vista
        model.addAttribute("comodidades", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "entities-html/comodidad";
    }

    @GetMapping("/comodidades/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Formulario nueva comodidad.");
        model.addAttribute("comodidad", new Comodidad());
        return "forms-html/comodidad-form";
    }

    @GetMapping("/comodidades/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando comodidad ID {}", id);
        Optional<Comodidad> comodidad = comodidadService.obtenerPorId(id);
        if (comodidad.isPresent()) {
            model.addAttribute("comodidad", comodidad.get());
            return "forms-html/comodidad-form";
        }
        return "redirect:/comodidades";
    }

    /**
     * Procesa el guardado de una nueva comodidad.
     * Incluye validación para evitar nombres duplicados.
     */
    @PostMapping("/comodidades/save")
    public String guardar(@Valid @ModelAttribute Comodidad comodidad,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forms-html/comodidad-form";
        }

        // Validación de duplicados (solo al crear nuevo)
        if (comodidad.getId() == null && comodidadService.existePorNombre(comodidad.getNombre())) {
            logger.warn("Intento de duplicado: {}", comodidad.getNombre());
            redirectAttributes.addFlashAttribute("errorMessage", "Ya existe una comodidad con ese nombre.");
            return "redirect:/comodidades/new";
        }

        comodidadService.guardar(comodidad);
        logger.info("Comodidad guardada: {}", comodidad.getNombre());
        redirectAttributes.addFlashAttribute("successMessage", "Comodidad guardada correctamente.");
        return "redirect:/comodidades";
    }

    @PostMapping("/comodidades/update")
    public String actualizar(@Valid @ModelAttribute Comodidad comodidad,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forms-html/comodidad-form";
        }

        try {
            comodidadService.actualizar(comodidad.getId(), comodidad);
            redirectAttributes.addFlashAttribute("successMessage", "Comodidad actualizada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar.");
        }
        return "redirect:/comodidades";
    }

    /**
     * Elimina una comodidad.
     * Controla la excepción si la comodidad está asignada a una guarida.
     */
    @GetMapping("/comodidades/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Eliminando comodidad ID {}", id);
        try {
            comodidadService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Comodidad eliminada.");
        } catch (Exception e) {
            // Error común: Intentar borrar una comodidad que está siendo usada por una Guarida
            logger.error("Error al eliminar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar: Probablemente esté asignada a una guarida.");
        }
        return "redirect:/comodidades";
    }

    /**
     * Método auxiliar para convertir el string de ordenación en un objeto Sort.
     */
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "nameAsc" -> Sort.by("nombre").ascending();
            case "nameDesc" -> Sort.by("nombre").descending();
            case "idDesc" -> Sort.by("id").descending();
            default -> Sort.by("id").ascending();
        };
    }

    // --- SECCIÓN API REST (DESACTIVADA) ---
    /*
     * NOTA DEL DESARROLLADOR:
     * La implementación de la API REST se ha pospuesto. Se ha priorizado el desarrollo
     * y comprensión del modelo MVC sobre la generación automática de endpoints,
     * dado que la capacidad técnica actual se centra en la estabilidad de la aplicación web.
     */

    /*
    @GetMapping("/api/comodidades")
    @ResponseBody
    public ResponseEntity<List<Comodidad>> obtenerTodasApi() {
        return ResponseEntity.ok(comodidadService.obtenerTodas());
    }

    @GetMapping("/api/comodidades/{id}")
    @ResponseBody
    public ResponseEntity<Comodidad> obtenerPorIdApi(@PathVariable Long id) {
        return comodidadService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/comodidades")
    @ResponseBody
    public ResponseEntity<Comodidad> crearApi(@Valid @RequestBody Comodidad comodidad) {
        try {
            if(comodidadService.existePorNombre(comodidad.getNombre())){
                 return ResponseEntity.badRequest().build(); // O mensaje personalizado
            }
            Comodidad nueva = comodidadService.guardar(comodidad);
            return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/comodidades/{id}")
    @ResponseBody
    public ResponseEntity<Comodidad> actualizarApi(@PathVariable Long id, @Valid @RequestBody Comodidad comodidad) {
        try {
            Comodidad actualizada = comodidadService.actualizar(id, comodidad);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/comodidades/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarApi(@PathVariable Long id) {
        try {
            comodidadService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // Conflict si tiene guaridas asociadas
        }
    }
    */
}