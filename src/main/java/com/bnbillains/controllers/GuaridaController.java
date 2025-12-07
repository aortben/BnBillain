package com.bnbillains.controllers;

import com.bnbillains.entities.Guarida;
import com.bnbillains.entities.SalaSecreta; // <--- Importante
import com.bnbillains.repositories.ComodidadRepository;
import com.bnbillains.services.FileStorageService;
import com.bnbillains.services.GuaridaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class GuaridaController {

    private static final Logger logger = LoggerFactory.getLogger(GuaridaController.class);

    private final GuaridaService guaridaService;
    private final ComodidadRepository comodidadRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public GuaridaController(GuaridaService guaridaService, ComodidadRepository comodidadRepository) {
        this.guaridaService = guaridaService;
        this.comodidadRepository = comodidadRepository;
    }

    // ==========================================
    // LISTADO Y DETALLES
    // ==========================================

    @GetMapping("/guaridas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String search,
                         @RequestParam(required = false) Double minPrice,
                         @RequestParam(required = false) Double maxPrice,
                         @RequestParam(required = false) String sort,
                         Model model) {

        Sort sortObj = getSort(sort);
        List<Guarida> todosLosResultados;

        if (minPrice != null && maxPrice != null) {
            todosLosResultados = guaridaService.buscarPorRangoPrecioOrdenado(minPrice, maxPrice, sortObj);
        } else if (search != null && !search.isBlank()) {
            todosLosResultados = guaridaService.buscarPorNombre(search);
        } else {
            todosLosResultados = guaridaService.obtenerTodosOrdenados(sortObj);
        }

        // Paginación Manual (Slice)
        int pageSize = 6;
        int totalItems = todosLosResultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<Guarida> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : todosLosResultados.subList(start, end);

        model.addAttribute("guaridas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "entities-html/guarida";
    }

    @GetMapping("/guaridas/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        Optional<Guarida> guarida = guaridaService.obtenerPorId(id);

        if (guarida.isEmpty()) {
            return "redirect:/guaridas";
        }

        Guarida g = guarida.get();
        model.addAttribute("guarida", g);
        model.addAttribute("comodidades", g.getComodidades() != null ? g.getComodidades() : Collections.emptyList());
        model.addAttribute("resenas", g.getResenas() != null ? g.getResenas() : Collections.emptyList());
        model.addAttribute("salaSecreta", g.getSalaSecreta());

        return "entities-html/guarida-detail";
    }

    // ==========================================
    // FORMULARIOS (CREAR / EDITAR)
    // ==========================================

    @GetMapping("/guaridas/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Formulario nueva guarida.");
        Guarida guarida = new Guarida();

        // ✅ IMPORTANTE: Inicializar SalaSecreta para que el form HTML pueda hacer *{salaSecreta.codigo}
        guarida.setSalaSecreta(new SalaSecreta());

        model.addAttribute("guarida", guarida);
        model.addAttribute("allComodidades", comodidadRepository.findAll());
        return "forms-html/guarida-form";
    }

    @GetMapping("/guaridas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando guarida ID {}", id);
        Optional<Guarida> optGuarida = guaridaService.obtenerPorId(id);

        if (optGuarida.isPresent()) {
            Guarida guarida = optGuarida.get();

            // ✅ IMPORTANTE: Si por error en BD es null, inicializarla para que no falle el form
            if (guarida.getSalaSecreta() == null) {
                guarida.setSalaSecreta(new SalaSecreta());
            }

            model.addAttribute("guarida", guarida);
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }
        return "redirect:/guaridas";
    }

    // ==========================================
    // ACCIONES (SAVE / UPDATE / DELETE)
    // ==========================================

    @PostMapping("/guaridas/save")
    public String guardar(@Valid @ModelAttribute Guarida guarida,
                          @RequestParam("imageFile") MultipartFile imageFile,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }

        // Validación de nombre único
        if (guarida.getId() == null && guaridaService.existePorNombre(guarida.getNombre())) {
            model.addAttribute("errorMessage", "El nombre de la guarida ya existe.");
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }

        // Subida de imagen
        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                guarida.setImagen(fileName);
            }
        }
        // Nota: Si no sube imagen, guarida.imagen será null, y la entidad usará la default.

        guaridaService.guardar(guarida);
        redirectAttributes.addFlashAttribute("successMessage", "Guarida guardada con éxito.");
        return "redirect:/guaridas";
    }

    @PostMapping("/guaridas/update")
    public String actualizar(@Valid @ModelAttribute Guarida guarida,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }

        try {
            // ✅ LÓGICA DE IMAGEN EN UPDATE:
            if (!imageFile.isEmpty()) {
                // 1. Si sube nueva foto, guardarla y setearla
                String fileName = fileStorageService.saveFile(imageFile);
                if (fileName != null) {
                    guarida.setImagen(fileName);
                }
            } else {
                // 2. Si NO sube foto, recuperar la antigua de BD para no perderla
                Optional<Guarida> guaridaAntigua = guaridaService.obtenerPorId(guarida.getId());
                guaridaAntigua.ifPresent(g -> guarida.setImagen(g.getImagen()));
            }

            guaridaService.actualizar(guarida.getId(), guarida);
            redirectAttributes.addFlashAttribute("successMessage", "Guarida actualizada correctamente.");

        } catch (Exception e) {
            logger.error("Error al actualizar guarida", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/guaridas";
    }

    @GetMapping("/guaridas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            guaridaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Guarida eliminada y sus reservas canceladas.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar la guarida.");
        }
        return "redirect:/guaridas";
    }

    // Helper
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "nameAsc" -> Sort.by("nombre").ascending();
            case "nameDesc" -> Sort.by("nombre").descending();
            case "priceAsc" -> Sort.by("precioNoche").ascending();
            case "priceDesc" -> Sort.by("precioNoche").descending();
            default -> Sort.by("id").ascending();
        };
    }
}