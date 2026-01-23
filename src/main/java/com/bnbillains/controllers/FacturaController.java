package com.bnbillains.controllers;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Villano;
import com.bnbillains.repositories.ReservaRepository;
import com.bnbillains.repositories.VillanoRepository;
import com.bnbillains.services.FacturaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

@Controller
public class FacturaController {

    private static final Logger logger = LoggerFactory.getLogger(FacturaController.class);

    private final FacturaService facturaService;
    private final ReservaRepository reservaRepository;
    private final VillanoRepository villanoRepository;

    public FacturaController(FacturaService facturaService, ReservaRepository reservaRepository, VillanoRepository villanoRepository) {
        this.facturaService = facturaService;
        this.reservaRepository = reservaRepository;
        this.villanoRepository = villanoRepository;
    }

    @GetMapping("/facturas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String metodoPago,
                         @RequestParam(required = false) Double minImporte,
                         @RequestParam(required = false) Double maxImporte,
                         @RequestParam(required = false) String sort,
                         Model model) {

        Sort sortObj = getSort(sort);
        List<Factura> resultados;

        if (minImporte != null && maxImporte != null) {
            resultados = facturaService.buscarPorRangoImporte(minImporte, maxImporte, sortObj);
        } else if (metodoPago != null && !metodoPago.isBlank()) {
            resultados = facturaService.buscarPorMetodoPago(metodoPago, sortObj);
        } else {
            resultados = facturaService.obtenerTodas(sortObj);
        }

        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<Factura> listaPaginada = (start > end || totalItems == 0) ? Collections.emptyList() : resultados.subList(start, end);

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

    @GetMapping("/facturas/new")
    public String formularioNuevo(Model model) {
        Factura f = new Factura();
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

            if (f.getFechaEmision() == null) {
                f.setFechaEmision(LocalDate.now());
            }

            model.addAttribute("factura", f);
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
            if (factura.getFechaEmision() == null) factura.setFechaEmision(LocalDate.now());
            facturaService.guardar(factura);
            redirectAttributes.addFlashAttribute("successMessage", "Factura emitida correctamente.");
        } catch (IllegalArgumentException e) {
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
            List<String> camposSoloLectura = List.of("fechaEmision", "importe", "impuestosMalignos", "reserva");

            boolean errorCritico = bindingResult.getFieldErrors().stream()
                    .anyMatch(err -> !camposSoloLectura.contains(err.getField()));

            if (errorCritico) {
                logger.warn("Errores de validación críticos detectados: {}", bindingResult.getAllErrors());
                model.addAttribute("allReservas", reservaRepository.findAll());

                if(factura.getId() != null) {
                    facturaService.obtenerPorId(factura.getId()).ifPresent(orig -> {
                        if(factura.getReserva() == null) factura.setReserva(orig.getReserva());
                        if(factura.getFechaEmision() == null) factura.setFechaEmision(orig.getFechaEmision());
                    });
                }
                return "forms-html/factura-form";
            }
            logger.info("Saltando validación estricta de campos readonly. Procediendo a actualizar.");
        }

        try {
            facturaService.actualizar(factura.getId(), factura);
            redirectAttributes.addFlashAttribute("successMessage", "✅ Estado de pago actualizado.");
        } catch (Exception e) {
            logger.error("Error al actualizar: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/facturas/" + factura.getId() + "/edit";
        }
        return "redirect:/facturas";
    }

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

    @GetMapping("/facturas/{id}/verDetalle")
    public String verDetalle(@PathVariable Long id, Model model) {
        return facturaService.obtenerPorId(id)
                .map(factura -> {
                    model.addAttribute("factura", factura);
                    return "entities-html/factura-detail";
                })
                .orElseGet(() -> "redirect:/facturas");
    }

    @GetMapping("/mis-facturas")
    public String misFacturas(@RequestParam(defaultValue = "1") int page,
                              @RequestParam(required = false) String sort,
                              Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "redirect:/login";
        }
        
        String username = authentication.getName();
        Optional<Villano> villano = villanoRepository.findByUsername(username);
        
        if (villano.isEmpty()) {
            logger.warn("Usuario autenticado {} no tiene villano asociado", username);
            return "redirect:/facturas";
        }
        
        List<Factura> resultados = facturaService.obtenerFacturasPorVillano(villano.get().getId());
        
        Sort sortObj = getSort(sort);
        resultados.sort((a, b) -> {
            if (sortObj.getOrderFor("fechaEmision") != null) {
                int cmp = a.getFechaEmision().compareTo(b.getFechaEmision());
                return sortObj.getOrderFor("fechaEmision").isAscending() ? cmp : -cmp;
            }
            if (sortObj.getOrderFor("importe") != null) {
                int cmp = Double.compare(a.getImporte(), b.getImporte());
                return sortObj.getOrderFor("importe").isAscending() ? cmp : -cmp;
            }
            return Long.compare(a.getId(), b.getId());
        });
        
        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<Factura> listaPaginada = (start > end || totalItems == 0) ? Collections.emptyList() : resultados.subList(start, end);
        
        model.addAttribute("facturas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("sort", sort);
        
        return "entities-html/factura";
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