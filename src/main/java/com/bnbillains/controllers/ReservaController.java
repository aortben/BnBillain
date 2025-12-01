package com.bnbillains.controllers;

import com.bnbillains.entities.Reserva;
import com.bnbillains.repositories.GuaridaRepository;
import com.bnbillains.repositories.VillanoRepository;
import com.bnbillains.services.ReservaService;
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
public class ReservaController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;
    // Necesarios para cargar los desplegables en el formulario
    private final VillanoRepository villanoRepository;
    private final GuaridaRepository guaridaRepository;

    public ReservaController(ReservaService reservaService,
                             VillanoRepository villanoRepository,
                             GuaridaRepository guaridaRepository) {
        this.reservaService = reservaService;
        this.villanoRepository = villanoRepository;
        this.guaridaRepository = guaridaRepository;
    }

    /**
     * Listado de Reservas con Paginación Manual, Filtros (Villano/Estado) y Ordenación.
     */
    @GetMapping("/reservas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) Long villanoId, // Filtro por Cliente
                         @RequestParam(required = false) Boolean estado, // Filtro Confirmada/Pendiente
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Listando reservas... Pag: {}, VillanoID: {}, Estado: {}", page, villanoId, estado);

        Sort sortObj = getSort(sort);
        List<Reserva> resultados;

        // 1. Lógica de Filtrado
        if (villanoId != null) {
            resultados = reservaService.buscarPorVillano(villanoId, sortObj);
        } else if (estado != null) {
            resultados = reservaService.buscarPorEstado(estado, sortObj);
        } else {
            resultados = reservaService.obtenerTodas(sortObj);
        }

        // 2. Paginación Manual
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<Reserva> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // 3. Pasar datos a la vista
        model.addAttribute("reservas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);

        // Filtros para mantener en la vista
        model.addAttribute("villanoId", villanoId);
        model.addAttribute("estado", estado);
        model.addAttribute("sort", sort);

        // Listas para los filtros (selects de búsqueda)
        model.addAttribute("allVillanos", villanoRepository.findAll());

        return "entities-html/reserva";
    }

    @GetMapping("/reservas/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Nueva reserva.");
        model.addAttribute("reserva", new Reserva());
        // Cargar listas para los desplegables del formulario
        model.addAttribute("allVillanos", villanoRepository.findAll());
        model.addAttribute("allGuaridas", guaridaRepository.findAll());
        return "forms-html/reserva-form";
    }

    @GetMapping("/reservas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando reserva ID {}", id);
        Optional<Reserva> reserva = reservaService.obtenerPorId(id);
        if (reserva.isPresent()) {
            model.addAttribute("reserva", reserva.get());
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }
        return "redirect:/reservas";
    }

    @PostMapping("/reservas/save")
    public String guardar(@Valid @ModelAttribute Reserva reserva,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // Si hay errores, hay que recargar los desplegables o el HTML se rompe
        if (bindingResult.hasErrors()) {
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }

        try {
            reservaService.guardar(reserva);
            logger.info("Reserva registrada para villano ID: {}", reserva.getVillano().getId());
            redirectAttributes.addFlashAttribute("successMessage", "Reserva creada con éxito.");
        } catch (IllegalArgumentException e) {
            // Error de fechas cruzadas
            logger.error("Error de validación: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/reservas/new";
        }

        return "redirect:/reservas";
    }

    @PostMapping("/reservas/update")
    public String actualizar(@Valid @ModelAttribute Reserva reserva,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }

        try {
            reservaService.actualizar(reserva.getId(), reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva modificada.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); // Fechas mal o ID no encontrado
        }
        return "redirect:/reservas";
    }

    @GetMapping("/reservas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Cancelando reserva ID {}", id);
        try {
            reservaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada y eliminada.");
        } catch (Exception e) {
            logger.error("Error al eliminar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar la reserva (¿Tiene factura?).");
        }
        return "redirect:/reservas";
    }

    // ordenacion
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").descending(); // Por defecto las más nuevas primero
        return switch (sort) {
            case "dateAsc" -> Sort.by("fechaInicio").ascending();
            case "dateDesc" -> Sort.by("fechaInicio").descending();
            case "costAsc" -> Sort.by("costeTotal").ascending();
            case "costDesc" -> Sort.by("costeTotal").descending();
            default -> Sort.by("id").descending();
        };
    }
}