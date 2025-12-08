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

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la gestión administrativa de facturas.
 * Permite listar, filtrar, editar estados de pago y visualizar documentos fiscales.
 */
@Controller
public class FacturaController {

    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaService facturaService;
    private final ReservaRepository reservaRepository;

    public FacturaController(FacturaService facturaService, ReservaRepository reservaRepository) {
        this.facturaService = facturaService;
        this.reservaRepository = reservaRepository;
    }

    // --- LISTAR ---

    /**
     * Muestra el listado de facturas con filtros avanzados.
     * Soporta filtrado por rango de precios y método de pago simultáneamente.
     */
    @GetMapping("/facturas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String metodoPago,
                         @RequestParam(required = false) Double minImporte,
                         @RequestParam(required = false) Double maxImporte,
                         @RequestParam(required = false) String sort,
                         Model model) {

        Sort sortObj = getSort(sort);
        List<Factura> resultados;

        // Lógica de filtrado en cascada (Prioridad: Rango > Método > Todo)
        if (minImporte != null && maxImporte != null) {
            resultados = facturaService.buscarPorRangoImporte(minImporte, maxImporte, sortObj);
        } else if (metodoPago != null && !metodoPago.isBlank()) {
            resultados = facturaService.buscarPorMetodoPago(metodoPago, sortObj);
        } else {
            resultados = facturaService.obtenerTodas(sortObj);
        }

        // Paginación Manual (Cálculo de sublistas para la vista)
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<Factura> listaPaginada = (start > end || totalItems == 0) ? Collections.emptyList() : resultados.subList(start, end);

        // Pasar datos al HTML
        model.addAttribute("facturas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("metodoPago", metodoPago);
        model.addAttribute("minImporte", minImporte);
        model.addAttribute("maxImporte", maxImporte);
        model.addAttribute("sort", sort);

        return "entities-html/factura";
    }

    // --- FORMULARIOS ---

    @GetMapping("/facturas/new")
    public String formularioNuevo(Model model) {
        Factura f = new Factura();
        // Inicializamos fecha HOY para evitar errores de validación en la vista
        f.setFechaEmision(LocalDate.now());

        model.addAttribute("factura", f);
        model.addAttribute("allReservas", reservaRepository.findAll());
        return "forms-html/factura-form";
    }

    @GetMapping("/facturas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Factura> facturaOpt = facturaService.obtenerPorId(id);
        if (facturaOpt.isPresent()) {
            Factura f = facturaOpt.get();

            // Aseguramos que la fecha no sea nula para el selector HTML
            if (f.getFechaEmision() == null) {
                f.setFechaEmision(LocalDate.now());
            }

            model.addAttribute("factura", f);
            model.addAttribute("allReservas", reservaRepository.findAll());
            return "forms-html/factura-form";
        }
        return "redirect:/facturas";
    }

    // --- GUARDAR ---

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
            // Seguridad extra: si llega sin fecha, ponemos hoy
            if (factura.getFechaEmision() == null) factura.setFechaEmision(LocalDate.now());
            facturaService.guardar(factura);
            redirectAttributes.addFlashAttribute("successMessage", "Factura emitida correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/facturas/new";
        }
        return "redirect:/facturas";
    }

    // --- ACTUALIZAR ---

    /**
     * Actualiza la factura existente.
     * Incluye lógica para ignorar errores de validación en campos de solo lectura
     * (como importes o fechas) si estos no se envían correctamente desde el formulario HTML.
     */
    @PostMapping("/facturas/update")
    public String actualizar(@Valid @ModelAttribute Factura factura,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        // 1. FILTRO INTELIGENTE DE ERRORES
        if (bindingResult.hasErrors()) {

            // Lista de campos 'readonly' cuyos errores podemos ignorar (porque los recuperaremos de la BD)
            List<String> camposSoloLectura = List.of("fechaEmision", "importe", "impuestosMalignos", "reserva");

            // Comprobamos si hay errores en campos críticos (los que SÍ editamos, como metodoPago)
            boolean errorCritico = bindingResult.getFieldErrors().stream()
                    .anyMatch(err -> !camposSoloLectura.contains(err.getField()));

            if (errorCritico) {
                logger.warn("Errores de validación críticos detectados: {}", bindingResult.getAllErrors());
                model.addAttribute("allReservas", reservaRepository.findAll());

                // Restauramos datos visuales recuperando de la BD para no romper el formulario
                if(factura.getId() != null) {
                    facturaService.obtenerPorId(factura.getId()).ifPresent(orig -> {
                        if(factura.getReserva() == null) factura.setReserva(orig.getReserva());
                        if(factura.getFechaEmision() == null) factura.setFechaEmision(orig.getFechaEmision());
                    });
                }
                return "forms-html/factura-form";
            }
            // Si llegamos aquí, los errores eran solo de campos readonly. Los ignoramos.
            logger.info("Saltando validación estricta de campos readonly. Procediendo a actualizar.");
        }

        try {
            // Llamamos al servicio "blindado" que protege los datos
            facturaService.actualizar(factura.getId(), factura);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Estado de pago actualizado.");
        } catch (Exception e) {
            logger.error("Error al actualizar: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/facturas/" + factura.getId() + "/edit";
        }
        return "redirect:/facturas";
    }

    // --- EXTRAS ---

    @GetMapping("/facturas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            facturaService.eliminar(id);
            redirectAttributes.addFlashAttribute("successMessage", "Factura eliminada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "No se pudo eliminar.");
        }
        return "redirect:/facturas";
    }

    /**
     * Carga la vista de detalle para impresión (PDF).
     */
    @GetMapping("/facturas/{id}/verDetalle")
    public String verDetalle(@PathVariable Long id, Model model) {
        return facturaService.obtenerPorId(id)
                .map(factura -> {
                    model.addAttribute("factura", factura);
                    return "entities-html/factura-detail";
                })
                .orElseGet(() -> "redirect:/facturas");
    }

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