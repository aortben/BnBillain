package com.bnbillains.controllers;

import com.bnbillains.entities.Resena;
import com.bnbillains.services.ResenaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @GetMapping("/resena")
    public String listar(Model model) {
        List<Resena> resenas = resenaService.obtenerTodas();
        model.addAttribute("resenas", resenas);
        return "entities-html/resena";
    }

    @GetMapping("/resena/new")
    public String formularioNuevo(Model model) {
        model.addAttribute("resena", new Resena());
        return "forms-html/resena-form";
    }

    @GetMapping("/resena/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Resena> resena = resenaService.obtenerPorId(id);
        if (resena.isPresent()) {
            model.addAttribute("resena", resena.get());
            return "forms-html/resena-form";
        }
        return "redirect:/resena";
    }

    @PostMapping("/resena/save")
    public String guardar(@Valid @ModelAttribute Resena resena) {
        resenaService.guardar(resena);
        return "redirect:/resena";
    }

    @PostMapping("/resena/update")
    public String actualizar(@Valid @ModelAttribute Resena resena) {
        resenaService.actualizar(resena.getId(), resena);
        return "redirect:/resena";
    }

    @GetMapping("/resena/{id}/delete")
    public String eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return "redirect:/resena";
    }

    /* ENDPOINTS REST (COMENTADO)
     * Si necesitas consumir datos en JSON desde JavaScript o apps externas:
     * 
     * Ejemplo con fetch en JavaScript:
     * fetch('/api/resenas')
     *   .then(response => response.json())
     *   .then(resenas => console.log(resenas));
     *
     * Para habilitar, descomenta los métodos abajo y añade las importaciones:
     * import org.springframework.http.HttpStatus;
     * import org.springframework.http.ResponseEntity;
     *
    @GetMapping("/api/resenas")
    @ResponseBody
    public ResponseEntity<List<Resena>> obtenerTodasApi() {
        return ResponseEntity.ok(resenaService.obtenerTodas());
    }

    @GetMapping("/api/resenas/{id}")
    @ResponseBody
    public ResponseEntity<Resena> obtenerPorIdApi(@PathVariable Long id) {
        Optional<Resena> resena = resenaService.obtenerPorId(id);
        return resena.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/resenas")
    @ResponseBody
    public ResponseEntity<Resena> crearApi(@Valid @RequestBody Resena resena) {
        try {
            Resena nuevaResena = resenaService.guardar(resena);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaResena);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/resenas/{id}")
    @ResponseBody
    public ResponseEntity<Resena> actualizarApi(@PathVariable Long id, @Valid @RequestBody Resena resena) {
        try {
            Resena resenaActualizada = resenaService.actualizar(id, resena);
            return ResponseEntity.ok(resenaActualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/resenas/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarApi(@PathVariable Long id) {
        try {
            resenaService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    */
}
