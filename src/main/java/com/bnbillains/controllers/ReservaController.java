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
    private final VillanoRepository villanoRepository;
    private final GuaridaRepository guaridaRepository;

    public ReservaController(ReservaService reservaService,
                             VillanoRepository villanoRepository,
                             GuaridaRepository guaridaRepository) {
        this.reservaService = reservaService;
        this.villanoRepository = villanoRepository;
        this.guaridaRepository = guaridaRepository;
    }

    @GetMapping("/reservas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) Long villanoId,
                         @RequestParam(required = false) Boolean estado,
                         @RequestParam(required = false) String sort,
                         Model model) {

        Sort sortObj = getSort(sort);
        List<Reserva> resultados;
        if (villanoId != null) {
            resultados = reservaService.buscarPorVillano(villanoId, sortObj);
        }  else if (estado != null){
            resultados = reservaService.buscarPorEstado(estado, sortObj);
        } else{
            resultados = reservaService.obtenerTodas(sortObj);
        }

        int pageSize = 5;
        int totalItems = resultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;
        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<Reserva> listaPaginada = (start > end || totalItems == 0) ? Collections.emptyList() : resultados.subList(start, end);

        model.addAttribute("reservas", listaPaginada);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("allVillanos", villanoRepository.findAll());
        model.addAttribute("villanoId", villanoId);
        model.addAttribute("estado", estado);
        model.addAttribute("sort", sort);
        return "entities-html/reserva";
    }

    @GetMapping("/reservas/new")
    public String formularioNuevo(Model model) {
        model.addAttribute("reserva", new Reserva());
        model.addAttribute("allVillanos", villanoRepository.findAll());
        model.addAttribute("allGuaridas", guaridaRepository.findAll());
        return "forms-html/reserva-form";
    }

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

    @PostMapping("/reservas/save")
    public String guardar(@Valid @ModelAttribute Reserva reserva,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allVillanos", villanoRepository.findAll());
            model.addAttribute("allGuaridas", guaridaRepository.findAll());
            return "forms-html/reserva-form";
        }

        try {
            reservaService.guardar(reserva);
            redirectAttributes.addFlashAttribute("successMessage", "Reserva confirmada y Factura generada.");
        } catch (Exception e) { // CAMBIO: Capturamos cualquier error para verlo en pantalla
            e.printStackTrace(); // Imprime error en consola
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
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
            redirectAttributes.addFlashAttribute("successMessage", "Reserva y Factura actualizadas.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
            return "redirect:/reservas/" + reserva.getId() + "/edit";
        }
        return "redirect:/reservas";
    }

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

    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").descending();
        return switch (sort) {
            case "dateAsc" -> Sort.by("fechaInicio").ascending();
            case "dateDesc" -> Sort.by("fechaInicio").descending();
            default -> Sort.by("id").descending();
        };
    }
}