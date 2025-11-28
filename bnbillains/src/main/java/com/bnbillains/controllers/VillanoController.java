package com.bnbillains.controllers;

import com.bnbillains.entities.Villano;
import com.bnbillains.services.VillanoService;
import com.bnbillains.services.ReservaService;
import com.bnbillains.services.ResenaService;
import com.bnbillains.services.FacturaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

@Controller
public class VillanoController {

    private final VillanoService villanoService;
    private final ReservaService reservaService;
    private final ResenaService resenaService;
    private final FacturaService facturaService;

    public VillanoController(VillanoService villanoService, ReservaService reservaService, 
                             ResenaService resenaService, FacturaService facturaService) {
        this.villanoService = villanoService;
        this.reservaService = reservaService;
        this.resenaService = resenaService;
        this.facturaService = facturaService;
    }

    @GetMapping("/villano")
    public String listar(Model model) {
        List<Villano> villanos = villanoService.obtenerTodas();
        model.addAttribute("villanos", villanos);
        return "entities-html/villano";
    }

    @GetMapping("/villano/new")
    public String formularioNuevo(Model model) {
        model.addAttribute("villano", new Villano());
        return "forms-html/villano-form";
    }

    @GetMapping("/villano/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        Optional<Villano> villano = villanoService.obtenerPorId(id);
        if (villano.isPresent()) {
            model.addAttribute("villano", villano.get());
            return "forms-html/villano-form";
        }
        return "redirect:/villano";
    }

    @PostMapping("/villano/save")
    public String guardar(@Valid @ModelAttribute Villano villano) {
        villanoService.guardar(villano);
        return "redirect:/villano";
    }

    @PostMapping("/villano/update")
    public String actualizar(@Valid @ModelAttribute Villano villano) {
        villanoService.actualizar(villano.getId(), villano);
        return "redirect:/villano";
    }

    @GetMapping("/villano/{id}/delete")
    public String eliminar(@PathVariable Long id) {
        villanoService.eliminar(id);
        return "redirect:/villano";
    }

    @GetMapping("/villano/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        Optional<Villano> villano = villanoService.obtenerPorId(id);
        if (villano.isPresent()) {
            model.addAttribute("villano", villano.get());
            model.addAttribute("reservas", reservaService.obtenerReservasPorVillano(id));
            model.addAttribute("resenas", resenaService.obtenerResenasPorVillano(id));
            model.addAttribute("facturas", facturaService.obtenerFacturasPorVillano(id));
            return "entities-html/villano-detail";
        }
        return "redirect:/villano";
    }

    /* ENDPOINTS REST (COMENTADO)
     * Si necesitas consumir datos en JSON desde JavaScript o apps externas:
     * 
     * Ejemplo con fetch en JavaScript:
     * fetch('/api/villanos')
     *   .then(response => response.json())
     *   .then(villanos => console.log(villanos));
     *
     * Para habilitar, descomenta los métodos abajo y añade las importaciones:
     * import org.springframework.http.HttpStatus;
     * import org.springframework.http.ResponseEntity;
     *
    @GetMapping("/api/villanos")
    @ResponseBody
    public ResponseEntity<List<Villano>> obtenerTodasApi() {
        return ResponseEntity.ok(villanoService.obtenerTodas());
    }

    @GetMapping("/api/villanos/{id}")
    @ResponseBody
    public ResponseEntity<Villano> obtenerPorIdApi(@PathVariable Long id) {
        Optional<Villano> villano = villanoService.obtenerPorId(id);
        return villano.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/api/villanos")
    @ResponseBody
    public ResponseEntity<Villano> crearApi(@Valid @RequestBody Villano villano) {
        try {
            Villano nuevoVillano = villanoService.guardar(villano);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVillano);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/api/villanos/{id}")
    @ResponseBody
    public ResponseEntity<Villano> actualizarApi(@PathVariable Long id, @Valid @RequestBody Villano villano) {
        try {
            Villano villanoActualizado = villanoService.actualizar(id, villano);
            return ResponseEntity.ok(villanoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/villanos/{id}")
    @ResponseBody
    public ResponseEntity<Void> eliminarApi(@PathVariable Long id) {
        try {
            villanoService.eliminar(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/villanos/email/{email}")
    @ResponseBody
    public ResponseEntity<Villano> obtenerPorEmailApi(@PathVariable String email) {
        Optional<Villano> villano = villanoService.buscarPorEmail(email);
        return villano.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/api/villanos/carnet/{carnet}")
    @ResponseBody
    public ResponseEntity<Villano> obtenerPorCarnetApi(@PathVariable String carnet) {
        Optional<Villano> villano = villanoService.buscarPorCarnetDeVillano(carnet);
        return villano.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
    */
}
