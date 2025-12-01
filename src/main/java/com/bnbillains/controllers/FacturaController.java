package com.bnbillains.controllers;

import com.bnbillains.entities.Factura;
import com.bnbillains.services.FacturaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/factura")
    public String listar(Model model) {
        List<Factura> facturas = facturaService.obtenerTodas();
        model.addAttribute("facturas", facturas);
        return "entities-html/factura";
    }

    @GetMapping("/factura/new")
    public String formularioNuevo(Model model) {
        model.addAttribute("factura", new Factura());
        return "forms-html/factura-form";
    }

    @GetMapping("/factura/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Factura> factura = facturaService.obtenerPorId(id);
        if (factura.isPresent()) {
            model.addAttribute("factura", factura.get());
            return "forms-html/factura-form";
        }
        return "redirect:/factura";
    }

    @PostMapping("/factura/save")
    public String guardar(@Valid @ModelAttribute Factura factura) {
        facturaService.guardar(factura);
        return "redirect:/factura";
    }

    @PostMapping("/factura/update")
    public String actualizar(@Valid @ModelAttribute Factura factura) {
        facturaService.actualizar(factura.getId(), factura);
        return "redirect:/factura";
    }

    @GetMapping("/factura/{id}/delete")
    public String eliminar(@PathVariable Long id) {
        facturaService.eliminar(id);
        return "redirect:/factura";
    }

    /* ENDPOINTS REST (COMENTADO)
     * Si necesitas consumir datos en JSON desde JavaScript o apps externas:
     * 
     * Ejemplo con fetch en JavaScript:
     * fetch('/api/facturas')
     *   .then(response => response.json())
     *   .then(facturas => console.log(facturas));
     *
     * Para habilitar, descomenta los métodos abajo y añade las importaciones:
     * import org.springframework.http.HttpStatus;
     * import org.springframework.http.ResponseEntity;
     *
    @GetMapping("/api/facturas")
    @ResponseBody
    public ResponseEntity<List<Factura>> obtenerTodasApi() {
        return ResponseEntity.ok(facturaService.obtenerTodas());
    }

    @GetMapping("/api/facturas/{id}")
    @ResponseBody
    public ResponseEntity<Factura> obtenerPorIdApi(@PathVariable Long id) {
        Optional<Factura> factura = facturaService.obtenerPorId(id);
        return factura.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/facturas")
    @ResponseBody
    public ResponseEntity<Factura> crearApi(@Valid @RequestBody Factura factura) {
        try {
            Factura nuevaFactura = facturaService.guardar(factura);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaFactura);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/facturas/{id}")
    @ResponseBody
    public ResponseEntity<Factura> actualizarApi(@PathVariable Long id, @Valid @RequestBody Factura factura) {
        try {
            Factura facturaActualizada = facturaService.actualizar(id, factura);
            return ResponseEntity.ok(facturaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/facturas/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarApi(@PathVariable Long id) {
        try {
            facturaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    */
}
