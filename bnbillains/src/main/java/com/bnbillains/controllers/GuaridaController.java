package com.bnbillains.controllers;

import com.bnbillains.entities.Guarida;
import com.bnbillains.services.GuaridaService;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/guaridas")
public class GuaridaController {

    private final GuaridaService guaridaService;

    public GuaridaController(GuaridaService guaridaService) {
        this.guaridaService = guaridaService;
    }

    @GetMapping
    public ResponseEntity<List<Guarida>> obtenerTodas() {
        return ResponseEntity.ok(guaridaService.obtenerTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Guarida> obtenerPorId(@PathVariable Long id) {
        Optional<Guarida> guarida = guaridaService.obtenerPorId(id);
        return guarida.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Guarida> crear(@Valid @RequestBody Guarida guarida) {
        try {
            Guarida nuevaGuarida = guaridaService.guardar(guarida);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaGuarida);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Guarida> actualizar(@PathVariable Long id, @Valid @RequestBody Guarida guarida) {
        try {
            Guarida guaridaActualizada = guaridaService.actualizar(id, guarida);
            return ResponseEntity.ok(guaridaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        try {
            guaridaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ubicacion/{ubicacion}")
    public ResponseEntity<List<Guarida>> obtenerPorUbicacion(@PathVariable String ubicacion) {
        List<Guarida> guaridas = guaridaService.obtenerPorUbicacion(ubicacion);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Guarida>> buscarPorNombre(@RequestParam String nombre) {
        List<Guarida> guaridas = guaridaService.buscarPorNombre(nombre);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/precio")
    public ResponseEntity<List<Guarida>> buscarPorRangoPrecio(
            @RequestParam Double min,
            @RequestParam Double max) {
        List<Guarida> guaridas = guaridaService.buscarPorRangoPrecio(min, max);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/precio/ordenado")
    public ResponseEntity<List<Guarida>> buscarPorRangoPrecionOrdenado(
            @RequestParam Double min,
            @RequestParam Double max,
            @RequestParam(defaultValue = "precioNoche") String sortBy) {
        Sort sort = Sort.by(sortBy);
        List<Guarida> guaridas = guaridaService.buscarPorRangoPrecioOrdenado(min, max, sort);
        return guaridas.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(guaridas);
    }

    @GetMapping("/ordenadas")
    public ResponseEntity<List<Guarida>> obtenerTodosOrdenados(
            @RequestParam(defaultValue = "precioNoche") String sortBy) {
        Sort sort = Sort.by(sortBy);
        List<Guarida> guaridas = guaridaService.obtenerTodosOrdenados(sort);
        return ResponseEntity.ok(guaridas);
    }
}
