package com.bnbillains.controllers;

import com.bnbillains.entities.Guarida;
import com.bnbillains.repositories.ComodidadRepository;
import com.bnbillains.services.FileStorageService;
import com.bnbillains.services.GuaridaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class GuaridaController {

    private static final Logger logger = LoggerFactory.getLogger(GuaridaController.class);

    private final GuaridaService guaridaService;
    private final ComodidadRepository comodidadRepository; // Necesario para los checkboxes del formulario

    @Autowired
    private FileStorageService fileStorageService;
    // Constructor
    public GuaridaController(GuaridaService guaridaService, ComodidadRepository comodidadRepository) {
        this.guaridaService = guaridaService;
        this.comodidadRepository = comodidadRepository;
    }

    //MVC

    /**
     * Listado Web: Incluye Paginación Manual, Filtros y Ordenación.
     */
    @GetMapping("/guaridas")
    public String listar(@RequestParam(defaultValue = "1") int page,
                         @RequestParam(required = false) String search,
                         @RequestParam(required = false) Double minPrice,
                         @RequestParam(required = false) Double maxPrice,
                         @RequestParam(required = false) String sort,
                         Model model) {

        logger.info("WEB: Solicitando catálogo... Pag: {}, Search: {}, Sort: {}", page, search, sort);

        Sort sortObj = getSort(sort);
        List<Guarida> todosLosResultados;

        // 1. Lógica de Filtrado (Usando los métodos que devuelven List)
        if (minPrice != null && maxPrice != null) {
            todosLosResultados = guaridaService.buscarPorRangoPrecioOrdenado(minPrice, maxPrice, sortObj);
        } else if (search != null && !search.isBlank()) {
            todosLosResultados = guaridaService.buscarPorNombre(search);
        } else {
            todosLosResultados = guaridaService.obtenerTodosOrdenados(sortObj);
        }

        // 2. Paginación Manual (Slice en memoria)
        int pageSize = 5;
        int totalItems = todosLosResultados.size();
        int totalPages = (int) Math.ceil((double) totalItems / pageSize);
        if (page > totalPages && totalPages > 0) page = totalPages;
        if (page < 1) page = 1;

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, totalItems);
        List<Guarida> listaPaginada = (start > end || totalItems == 0) ?
                Collections.emptyList() : todosLosResultados.subList(start, end);

        // 3. Pasar datos a la vista
        model.addAttribute("guaridas", listaPaginada); // Ojo: en tu HTML usas 'guaridas' o 'listGuaridas'? Ajustalo.
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "entities-html/guarida";
    }

    @GetMapping("/guaridas/{id}")
    public String detalle(@PathVariable Long id, Model model) {
        logger.info("WEB: Mostrando detalle guarida ID {}", id);
        Optional<Guarida> guarida = guaridaService.obtenerPorId(id);
        
        if (guarida.isEmpty()) {
            return "redirect:/guaridas";
        }
        
        Guarida g = guarida.get();
        model.addAttribute("guarida", g);
        model.addAttribute("comodidades", g.getComodidades() != null ? g.getComodidades() : Collections.emptyList());
        model.addAttribute("resenas", g.getResenas() != null ? g.getResenas() : Collections.emptyList());
        model.addAttribute("salaSecreta", g.getSalaSecreta());
        
        return "entities-html/guarida-detail";
    }

    @GetMapping("/guaridas/new")
    public String formularioNuevo(Model model) {
        logger.info("WEB: Formulario nueva guarida.");
        model.addAttribute("guarida", new Guarida());
        model.addAttribute("allComodidades", comodidadRepository.findAll());
        return "forms-html/guarida-form";
    }

    @GetMapping("/guaridas/{id}/edit")
    public String formularioEditar(@PathVariable Long id, Model model) {
        logger.info("WEB: Editando guarida ID {}", id);
        Optional<Guarida> guarida = guaridaService.obtenerPorId(id);
        if (guarida.isPresent()) {
            model.addAttribute("guarida", guarida.get());
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }
        return "redirect:/guaridas";
    }

    @PostMapping("/guaridas/save")
    public String guardar(@Valid @ModelAttribute Guarida guarida, @RequestParam("imageFile") MultipartFile imageFile,
                          BindingResult bindingResult,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }

        if (guarida.getId() == null && guaridaService.existePorNombre(guarida.getNombre())) {
            redirectAttributes.addFlashAttribute("errorMessage", "El nombre ya existe.");
            return "redirect:/guaridas/new";
        }

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                guarida.setImagen(fileName); // Guardar el nombre del archivo en la entidad
            }
        }

        guaridaService.guardar(guarida); // Aquí guarda la URL de la imagen tal cual viene del form
        redirectAttributes.addFlashAttribute("successMessage", "Guarida guardada con éxito.");
        return "redirect:/guaridas";
    }

    @PostMapping("/guaridas/update")
    public String actualizar(@Valid @ModelAttribute Guarida guarida, @RequestParam("imageFile") MultipartFile imageFile,
                             BindingResult bindingResult,
                             RedirectAttributes redirectAttributes,
                             Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allComodidades", comodidadRepository.findAll());
            return "forms-html/guarida-form";
        }

        if (!imageFile.isEmpty()) {
            String fileName = fileStorageService.saveFile(imageFile);
            if (fileName != null) {
                guarida.setImagen(fileName); // Guardar el nombre del archivo en la entidad
            }
        }

        try {
            guaridaService.actualizar(guarida.getId(), guarida); // Solo actualiza datos + URL imagen
            redirectAttributes.addFlashAttribute("successMessage", "Guarida actualizada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar.");
        }
        return "redirect:/guaridas";
    }
    // mantenemos GET
    // pero idealmente debería ser POST para evitar borrados accidentales por bots.
    @GetMapping("/guaridas/delete/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        logger.info("WEB: Eliminando guarida ID {}", id);
        guaridaService.eliminar(id);
        redirectAttributes.addFlashAttribute("successMessage", "Guarida eliminada.");
        return "redirect:/guaridas";
    }

    //Seccion api JSON
    /*
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
    public ResponseEntity<List<Guarida>> buscarPorRangoPrecioApi(@RequestParam Double min, @RequestParam Double max) {
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
    public ResponseEntity<List<Guarida>> obtenerTodosOrdenadosApi(@RequestParam(defaultValue = "precioNoche") String sortBy) {
        Sort sort = Sort.by(sortBy);
        List<Guarida> guaridas = guaridaService.obtenerTodosOrdenados(sort);
        return ResponseEntity.ok(guaridas);
    }*/

    // Ordenar filtrado
    private Sort getSort(String sort) {
        if (sort == null) return Sort.by("id").ascending();
        return switch (sort) {
            case "nameAsc" -> Sort.by("nombre").ascending();
            case "nameDesc" -> Sort.by("nombre").descending();
            case "priceAsc" -> Sort.by("precioNoche").ascending();
            case "priceDesc" -> Sort.by("precioNoche").descending();
            default -> Sort.by("id").ascending();
        };
    }
}