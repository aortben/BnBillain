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

/**
 * Controlador encargado de gestionar las Reservas.
 * Maneja el flujo de alquiler, validación de formularios y gestión de errores de overbooking.
 */
@Controller
public class ReservaController {

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;
    private final VillanoRepository villanoRepository;
    private final GuaridaRepository guaridaRepository;

    /**
     * Inyección de dependencias necesaria para la lógica de reservas
     * y para poblar los selectores de Villanos y Guaridas en los formularios.
     */
    public ReservaController(ReservaService reservaService,
                             VillanoRepository villanoRepository,
                             GuaridaRepository guaridaRepository) {
        this.reservaService = reservaService;
        this.villanoRepository = villanoRepository;
        this.guaridaRepository = guaridaRepository;
    }

    /**
     * Listado de reservas con soporte para paginación, filtrado y ordenación.
     * Permite filtrar por Cliente (Villano) o por Estado de la reserva.
     */
    @GetMapping("/reservas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) Long villanoId,
                         @RequestParam(required = false) Boolean estado,
                         @RequestParam(required = false) String sort,
                         Model model) {

        Sort sortObj = getSort(sort);
        List<Reserva> resultados;

        // Selección de la estrategia de búsqueda según los filtros activos
        if (villanoId != null) {
            resultados = reservaService.buscarPorVillano(villanoId, sortObj);
        } else if (estado != null) {
            resultados = reservaService.buscarPorEstado(estado, sortObj);
        } else {
            resultados = reservaService.obtenerTodas(sortObj);
        }

        // Lógica de Paginación Manual (Slice de la lista completa)
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        // Ajuste de límites de página
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        // Creación de la sublista para la vista actual
        List<Reserva> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // Paso de atributos a la vista (Thymeleaf)
        model.addAttribute("reservas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        // Necesario para mantener los filtros en los enlaces de paginación
        model.addAttribute("allVillanos", villanoRepository.findAll());
        model.addAttribute("villanoId", villanoId);
        model.addAttribute("estado", estado);
        model.addAttribute("sort", sort);

        return "entities-html/reserva";
    }

    /**
     * Muestra el formulario para crear una nueva reserva.
     * Carga las listas de Villanos y Guaridas para los desplegables.
     */
    @GetMapping("/reservas/new")
    public String formularioNuevo(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("allVillanos", villanoRepository.findAll());
        model.addAttribute("allGuaridas", guaridaRepository.findAll());
        return "forms-html/reserva-form";
    }

    /**
     * Muestra el formulario de edición para una reserva existente.
     */
    @GetMapping("/reservas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Reserva> reserva = reservaService.obtenerPorId(id);
        if (reserva.isPresent()) {
            model.addAttribute("reserva", reserva.get());
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }
        return "redirect:/reservas";
    }

    /**
     * Procesa la creación de una reserva.
     * Gestiona errores de validación y captura excepciones de negocio (Overbooking).
     */
    @PostMapping("/reservas/save")
    public String guardar(@Valid @ModelAttribute Reserva reserva,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        // 1. Validación de campos básicos (Fechas nulas, objetos nulos, etc.)
        if (bindingResult.hasErrors()) {
            // Recargamos las listas para que el formulario se pinte correctamente al volver
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }

        try {
            // 2. Intentamos guardar invocando la lógica de negocio
            // Esto puede lanzar excepciones si las fechas están ocupadas
            reservaService.guardar(reserva);

            redirectAttributes.addFlashAttribute("successMessage", "Reserva confirmada y Factura generada.");
        } catch (Exception e) {
            // 3. Captura de errores de negocio (Ej: Conflicto de fechas)
            e.printStackTrace();
            // Mostramos el mensaje exacto del servicio ("La guarida está ocupada...")
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/reservas/new";
        }
        return "redirect:/reservas";
    }

    /**
     * Procesa la actualización de una reserva existente.
     * Verifica conflictos de fechas excluyendo la reserva actual.
     */
    @PostMapping("/reservas/update")
    public String actualizar(@Valid @ModelAttribute Reserva reserva,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        // 1. Validación de campos del formulario
        if (bindingResult.hasErrors()) {
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }

        try {
            // 2. Intentamos actualizar
            reservaService.actualizar(reserva.getId(), reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva y Factura actualizadas.");
        } catch (Exception e) {
            // 3. Captura de error si al cambiar fechas chocamos con otra reserva
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/reservas/" + reserva.getId() + "/edit";
        }
        return "redirect:/reservas";
    }

    /**
     * Elimina una reserva y sus dependencias (Factura) mediante cascada.
     */
    @GetMapping("/reservas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            reservaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva cancelada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar.");
        }
        return "redirect:/reservas";
    }

    /**
     * Helper para convertir los parámetros de ordenación de texto a objetos Sort.
     */
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").descending();
        return switch (sort) {
            case "dateAsc" -> Sort.by("fechaInicio").ascending();
            case "dateDesc" -> Sort.by("fechaInicio").descending();
            default -> Sort.by("id").descending();
        };
    }
}