package com.bnbillains.controllers;

import com.bnbillains.entities.Guarida;
import com.bnbillains.services.GuaridaService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
public class GuaridaController {

    private final GuaridaService guaridaService;

    public GuaridaController(GuaridaService guaridaService) {
        this.guaridaService = guaridaService;
    }

    @GetMapping("/guarida")
    public String listar(Model model) {
        List<Guarida> guaridas = guaridaService.obtenerTodas();
        model.addAttribute("guaridas", guaridas);
        return "entities-html/guarida";
    }

    @GetMapping("/guarida/new")
    public String formularioNuevo(Model model) {
        model.addAttribute("guarida", new Guarida());
        return "forms-html/guarida-form";
    }

    @GetMapping("/guarida/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Guarida> guarida = guaridaService.obtenerPorId(id);
        if (guarida.isPresent()) {
            model.addAttribute("guarida", guarida.get());
            return "forms-html/guarida-form";
        }
        return "redirect:/guarida";
    }

    @PostMapping("/guarida/save")
    public String guardar(@Valid @ModelAttribute Guarida guarida) {
        guaridaService.guardar(guarida);
        return "redirect:/guarida";
    }

    @PostMapping("/guarida/update")
    public String actualizar(@Valid @ModelAttribute Guarida guarida) {
        guaridaService.actualizar(guarida.getId(), guarida);
        return "redirect:/guarida";
    }

    @GetMapping("/guarida/{id}/delete")
    public String eliminar(@PathVariable Long id) {
        guaridaService.eliminar(id);
        return "redirect:/guarida";
    }

    @GetMapping("/api/guaridas")
    @ResponseBody
    public ResponseEntity<List<Guarida>> obtenerTodasApi() {
        return ResponseEntity.ok(guaridaService.obtenerTodas());
    }

    @GetMapping("/api/guaridas/{id}")
    @ResponseBody
    public ResponseEntity<Guarida> obtenerPorIdApi(@PathVariable Long id) {
        Optional<Guarida> guarida = guaridaService.obtenerPorId(id);
        return guarida.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/guaridas")
    @ResponseBody
    public ResponseEntity<Guarida> crearApi(@Valid @RequestBody Guarida guarida) {
        try {
            Guarida nuevaGuarida = guaridaService.guardar(guarida);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaGuarida);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/guaridas/{id}")
    @ResponseBody
    public ResponseEntity<Guarida> actualizarApi(@PathVariable Long id, @Valid @RequestBody Guarida guarida) {
        try {
            Guarida guaridaActualizada = guaridaService.actualizar(id, guarida);
            return ResponseEntity.ok(guaridaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/guaridas/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarApi(@PathVariable Long id) {
        try {
            guaridaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/guaridas/ubicacion/{ubicacion}")
    @ResponseBody
    public ResponseEntity<List<Guarida>> obtenerPorUbicacionApi(@PathVariable String ubicacion) {
        List<Guarida> guaridas = guaridaService.obtenerPorUbicacion(ubicacion);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/api/guaridas/buscar")
    @ResponseBody
    public ResponseEntity<List<Guarida>> buscarPorNombreApi(@RequestParam String nombre) {
        List<Guarida> guaridas = guaridaService.buscarPorNombre(nombre);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/api/guaridas/precio")
    @ResponseBody
    public ResponseEntity<List<Guarida>> buscarPorRangoPrecioApi(
            @RequestParam Double min,
            @RequestParam Double max) {
        List<Guarida> guaridas = guaridaService.buscarPorRangoPrecio(min, max);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/api/guaridas/precio/ordenado")
    @ResponseBody
    public ResponseEntity<List<Guarida>> buscarPorRangoPrecionOrdenadoApi(
            @RequestParam Double min,
            @RequestParam Double max,
            @RequestParam(defaultValue = "precioNoche") String sortBy) {
        Sort sort = Sort.by(sortBy);
        List<Guarida> guaridas = guaridaService.buscarPorRangoPrecioOrdenado(min, max, sort);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/api/guaridas/ordenadas")
    @ResponseBody
    public ResponseEntity<List<Guarida>> obtenerTodosOrdenadosApi(
            @RequestParam(defaultValue = "precioNoche") String sortBy) {
        Sort sort = Sort.by(sortBy);
        List<Guarida> guaridas = guaridaService.obtenerTodosOrdenados(sort);
        return ResponseEntity.ok(guaridas);
    }
}
