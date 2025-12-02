package com.bnbillains.controllers;

import com.bnbillains.entities.Factura;
import com.bnbillains.repositories.ReservaRepository;
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
public class FacturaController {

    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaService facturaService;
    private final ReservaRepository reservaRepository; // Para el select de reservas

    public FacturaController(FacturaService facturaService, ReservaRepository reservaRepository) {
        this.facturaService = facturaService;
        this.reservaRepository = reservaRepository;
    }


    /**
     * Listado de Facturas: Paginación Manual + Filtros (Método Pago / Importe) + Ordenación.
     */
    @GetMapping("/facturas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String metodoPago, // Filtro texto
                         @RequestParam(required = false) Double minImporte, // Filtro rango
                         @RequestParam(required = false) Double maxImporte,
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Listando facturas... Pag: {}, Metodo: {}, Sort: {}", page, metodoPago, sort);

        Sort sortObj = getSort(sort);
        List<Factura> resultados;

        // 1. Lógica de Filtro
        if (minImporte != null && maxImporte != null) {
            resultados = facturaService.buscarPorRangoImporte(minImporte, maxImporte, sortObj);
        } else if (metodoPago != null && !metodoPago.isBlank()) {
            resultados = facturaService.buscarPorMetodoPago(metodoPago, sortObj);
        } else {
            resultados = facturaService.obtenerTodas(sortObj);
        }

        // 2. Paginación Manual
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);

        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);

        List<Factura> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : resultados.subList(start, end);

        // 3. Pasar datos a la vista
        model.addAttribute("facturas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);

        // Mantener filtros
        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("minImporte", minImporte);
        model.addAttribute("maxImporte", maxImporte);
        model.addAttribute("sort", sort);

        return "entities-html/factura";
    }

    @GetMapping("/facturas/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Nueva factura.");
        model.addAttribute("factura", new Factura());
        model.addAttribute("allReservas", reservaRepository.findAll()); // Select de reservas
        return "forms-html/factura-form";
    }

    @GetMapping("/facturas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando factura ID {}", id);
        Optional<Factura> factura = facturaService.obtenerPorId(id);
        if (factura.isPresent()) {
            model.addAttribute("factura", factura.get());
            model.addAttribute("allReservas", reservaRepository.findAll());
            return "forms-html/factura-form";
        }
        return "redirect:/facturas";
    }

    @PostMapping("/facturas/save")
    public String guardar(@Valid @ModelAttribute Factura factura,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allReservas", reservaRepository.findAll());
            return "forms-html/factura-form";
        }

        try {
            facturaService.guardar(factura);
            logger.info("Factura emitida ID: {}", factura.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Factura emitida correctamente.");
        } catch (IllegalArgumentException e) {
            // Error: Reserva ya facturada
            logger.error("Error negocio: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/facturas/new";
        }

        return "redirect:/facturas";
    }

    @PostMapping("/facturas/update")
    public String actualizar(@Valid @ModelAttribute Factura factura,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allReservas", reservaRepository.findAll());
            return "forms-html/factura-form";
        }

        try {
            facturaService.actualizar(factura.getId(), factura);
            redirectAttributes.addFlashAttribute("successMessage", "Factura rectificada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar.");
        }
        return "redirect:/facturas";
    }

    @GetMapping("/facturas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Eliminando factura ID {}", id);
        try {
            facturaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Factura eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar.");
        }
        return "redirect:/facturas";
    }


    /**
     * Muestra la vista de "Documento Factura" lista para imprimir.
     */

    @GetMapping("/facturas/{id}/verDetalle")
    public String verDetalle(@PathVariable Long id, Model model) {
        logger.info("WEB: Generando vista de factura ID {}", id);
        return facturaService.obtenerPorId(id)
                .map(factura -> {
                    model.addAttribute("factura", factura); //mete la factura en la bandeja
                    return "entities-html/factura-detalle"; // Nueva plantilla que crearemos
                })
                .orElseGet(() -> {
                    logger.warn("Factura no encontrada"); //dejamos constancia en el log
                    return "redirect:/facturas"; //redirigimos a facturas
                });
    }




    // ordenacion
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").descending();
        return switch (sort) {
            case "dateAsc" -> Sort.by("fechaEmision").ascending();
            case "dateDesc" -> Sort.by("fechaEmision").descending();
            case "amountAsc" -> Sort.by("importe").ascending();
            case "amountDesc" -> Sort.by("importe").descending();
            default -> Sort.by("id").descending();
        };
    }
}