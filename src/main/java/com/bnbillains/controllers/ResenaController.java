package com.bnbillains.controllers;

import com.bnbillains.entities.Resena;
import com.bnbillains.repositories.GuaridaRepository;
import com.bnbillains.repositories.VillanoRepository;
import com.bnbillains.services.ResenaService;
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
public class ResenaController {

    private static final Logger logger = LoggerFactory.getLogger(ResenaController.class);

    private final ResenaService resenaService;
    private final VillanoRepository villanoRepository; // Para el select
    private final GuaridaRepository guaridaRepository; // Para el select

    public ResenaController(ResenaService resenaService,
                            VillanoRepository villanoRepository,
                            GuaridaRepository guaridaRepository) {
        this.resenaService = resenaService;
        this.villanoRepository = villanoRepository;
        this.guaridaRepository = guaridaRepository;
    }

    /**
     * Listado de Reseñas: Paginación + Filtros (Puntuación/Texto) + Ordenación.
     */
    @GetMapping("/resenas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String search, // Filtro por comentario
                         @RequestParam(required = false) Long puntuacion, // Filtro exacto (1-5)
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Listando reseñas... Pag: {}, Search: {}, Sort: {}", page, search, sort);

        Sort sortObj = getSort(sort);
        List<Resena> resultados;

        // 1. Obtención de datos
        if (puntuacion != null) {
            resultados = resenaService.buscarPorPuntuacion(puntuacion, sortObj);
        } else if (search != null && !search.isBlank()) {
            resultados = resenaService.buscarPorComentario(search, sortObj);
        } else {
            resultados = resenaService.obtenerTodas(sortObj);
        }

        // 2. Paginación Manual
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<Resena> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // 3. Pasar atributos
        model.addAttribute("resenas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("search", search);
        model.addAttribute("puntuacion", puntuacion);
        model.addAttribute("sort", sort);

        return "entities-html/resena"; // Ruta: templates/entities-html/resena.html
    }

    @GetMapping("/resenas/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Nueva reseña.");
        model.addAttribute("resena", new Resena());
        model.addAttribute("allVillanos", villanoRepository.findAll());
        model.addAttribute("allGuaridas", guaridaRepository.findAll());
        return "forms-html/resena-form";
    }

    @GetMapping("/resenas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Resena> resena = resenaService.obtenerPorId(id);
        if (resena.isPresent()) {
            model.addAttribute("resena", resena.get());
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/resena-form";
        }
        return "redirect:/resenas";
    }

    @PostMapping("/resenas/save")
    public String guardar(@Valid @ModelAttribute Resena resena,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/resena-form";
        }

        resenaService.guardar(resena);
        logger.info("Reseña guardada.");
        redirectAttributes.addFlashAttribute("successMessage", "Opinión publicada.");
        return "redirect:/resenas";
    }

    @PostMapping("/resenas/update")
    public String actualizar(@Valid @ModelAttribute Resena resena,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/resena-form";
        }

        try {
            resenaService.actualizar(resena.getId(), resena);
            redirectAttributes.addFlashAttribute("successMessage", "Reseña editada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar.");
        }
        return "redirect:/resenas";
    }

    @GetMapping("/resenas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Eliminando reseña ID {}", id);
        resenaService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Reseña eliminada.");
        return "redirect:/resenas";
    }

    // Helper de ordenación
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").descending();
        return switch (sort) {
            case "starsAsc" -> Sort.by("puntuacion").ascending();
            case "starsDesc" -> Sort.by("puntuacion").descending();
            case "dateAsc" -> Sort.by("fechaPublicacion").ascending();
            case "dateDesc" -> Sort.by("fechaPublicacion").descending();
            default -> Sort.by("id").descending();
        };
    }
}