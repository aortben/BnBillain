package com.bnbillains.controllers;

import com.bnbillains.entities.Villano;
import com.bnbillains.services.VillanoService;
import com.bnbillains.services.FacturaService;
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
public class VillanoController {

    private static final Logger logger = LoggerFactory.getLogger(VillanoController.class);
    private final VillanoService villanoService;
    private final FacturaService facturaService;

    public VillanoController(VillanoService villanoService, FacturaService facturaService) {
        this.villanoService = villanoService;
        this.facturaService = facturaService;
    }

    /**
     * Listado de Villanos: Paginación + Filtro (Nombre/Alias) + Ordenación.
     */
    @GetMapping("/villanos")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String search,
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Listando villanos... Pag: {}, Search: {}, Sort: {}", page, search, sort);

        Sort sortObj = getSort(sort);
        List<Villano> resultados;

        // 1. Obtención de datos
        if (search != null && !search.isBlank()) {
            resultados = villanoService.buscarFlexible(search, sortObj);
        } else {
            resultados = villanoService.obtenerTodas(sortObj);
        }

        // 2. Paginación Manual
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<Villano> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // 3. Pasar atributos
        model.addAttribute("villanos", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);

        return "entities-html/villano"; // Ruta: templates/entities-html/villano.html
    }

    @GetMapping("/villanos/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        logger.info("WEB: Mostrando detalle villano ID {}", id);
        Optional<Villano> villano = villanoService.obtenerPorId(id);
        
        if (villano.isEmpty()) {
            return "redirect:/villanos";
        }
        
        Villano v = villano.get();
        model.addAttribute("villano", v);
        model.addAttribute("reservas", v.getReservasRealizadas() != null ? v.getReservasRealizadas() : Collections.emptyList());
        model.addAttribute("resenas", v.getResenasEscritas() != null ? v.getResenasEscritas() : Collections.emptyList());
        model.addAttribute("facturas", facturaService.obtenerFacturasPorVillano(id));
        
        return "entities-html/villano-detail";
    }

    @GetMapping("/villanos/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Nuevo villano.");
        model.addAttribute("villano", new Villano());
        return "forms-html/villano-form";
    }

    @GetMapping("/villanos/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando villano ID {}", id);
        Optional<Villano> villano = villanoService.obtenerPorId(id);
        if (villano.isPresent()) {
            model.addAttribute("villano", villano.get());
            return "forms-html/villano-form";
        }
        return "redirect:/villanos";
    }

    @PostMapping("/villanos/save")
    public String guardar(@Valid @ModelAttribute Villano villano,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forms-html/villano-form";
        }

        // Validación de duplicados (Carnet o Email)
        if (villano.getId() == null) {
            if (villanoService.existePorCarnetDeVillano(villano.getCarnetDeVillano())) {
                redirectAttributes.addFlashAttribute("errorMessage", "El carnet de villano ya existe.");
                return "redirect:/villanos/new";
            }
            if (villanoService.existePorEmail(villano.getEmail())) {
                redirectAttributes.addFlashAttribute("errorMessage", "El email ya está registrado.");
                return "redirect:/villanos/new";
            }
        }

        villanoService.guardar(villano);
        logger.info("Villano registrado: {}", villano.getAlias());
        redirectAttributes.addFlashAttribute("successMessage", "Villano registrado correctamente.");
        return "redirect:/villanos";
    }

    @PostMapping("/villanos/update")
    public String actualizar(@Valid @ModelAttribute Villano villano,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "forms-html/villano-form";
        }

        try {
            villanoService.actualizar(villano.getId(), villano);
            redirectAttributes.addFlashAttribute("successMessage", "Villano actualizado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar.");
        }
        return "redirect:/villanos";
    }

    @GetMapping("/villanos/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Eliminando villano ID {}", id);
        try {
            villanoService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Villano eliminado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se puede eliminar (tiene reservas o reseñas).");
        }
        return "redirect:/villanos";
    }

    // Helper de ordenación
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "nameAsc" -> Sort.by("nombre").ascending();
            case "nameDesc" -> Sort.by("nombre").descending();
            case "aliasAsc" -> Sort.by("alias").ascending();
            case "aliasDesc" -> Sort.by("alias").descending();
            default -> Sort.by("id").ascending();
        };
    }
}