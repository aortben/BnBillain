package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
import com.bnbillains.repositories.GuaridaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // ¡Importante!

import java.util.List;
import java.util.Optional;

@Service
public class GuaridaService {

    private final GuaridaRepository guaridaRepository;
    private final FileStorageService fileStorageService; // Inyectamos tu servicio de archivos

    // Constructor con ambas dependencias
    public GuaridaService(GuaridaRepository guaridaRepository, FileStorageService fileStorageService) {
        this.guaridaRepository = guaridaRepository;
        this.fileStorageService = fileStorageService;
    }

    // --- MÉTODOS DE LECTURA (INTACTOS) ---

    public List<Guarida> obtenerTodas() {
        return guaridaRepository.findAll();
    }

    public Optional<Guarida> obtenerPorId(Long id) {
        return guaridaRepository.findById(id);
    }

    // metodos de estructura con imagen

    /**
     * Guardar con imagen.
     * Transforma el MultipartFile en un String (nombre de archivo) usando FileStorageService.
     */
    public Guarida guardar(Guarida guarida, MultipartFile imagenFile) {
        // Si nos envían un archivo, lo procesamos
        if (imagenFile != null && !imagenFile.isEmpty()) {
            // Aquí ocurre la magia: El archivo se guarda y nos devuelve el STRING del nombre
            String nombreImagen = fileStorageService.saveFile(imagenFile);
            guarida.setImagen(nombreImagen);
        }
        return guaridaRepository.save(guarida);
    }

    /**
     * Actualizar con imagen.
     * Si viene imagen nueva, borra la vieja (el String anterior) y pone la nueva.
     */
    public Guarida actualizar(Long id, Guarida guarida, MultipartFile imagenFile) {
        return guaridaRepository.findById(id)
                .map(g -> {
                    g.setNombre(guarida.getNombre());
                    g.setDescripcion(guarida.getDescripcion());
                    g.setUbicacion(guarida.getUbicacion());
                    g.setPrecioNoche(guarida.getPrecioNoche());

                    // LÓGICA DE LA IMAGEN (STRING)
                    if (imagenFile != null && !imagenFile.isEmpty()) {
                        // 1. Borramos la evidencia anterior
                        fileStorageService.deleteFile(g.getImagen());
                        // 2. Guardamos la nueva y obtenemos el nuevo String
                        String nuevoNombre = fileStorageService.saveFile(imagenFile);
                        g.setImagen(nuevoNombre);
                    }
                    // Si no envían imagen, mantenemos el String que ya tenía (g.getImagen())

                    g.setComodidades(guarida.getComodidades());
                    g.setSalaSecreta(guarida.getSalaSecreta());
                    return guaridaRepository.save(g);
                })
                .orElseThrow(() -> new IllegalArgumentException("Guarida no encontrada"));
    }

    /**
     * Eliminar.
     * Ahora también borra el archivo físico usando el String almacenado.
     */
    public void eliminar(Long id) {
        guaridaRepository.findById(id).ifPresent(g -> {
            // Usamos el String de la BD para encontrar y borrar el archivo real
            fileStorageService.deleteFile(g.getImagen());
            guaridaRepository.deleteById(id);
        });
    }

    // Metodos de busqueda

    public List<Guarida> obtenerPorUbicacion(String ubicacion) {
        return guaridaRepository.findByUbicacion(ubicacion);
    }

    public List<Guarida> buscarPorNombre(String nombre) {
        return guaridaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Guarida> buscarPorRangoPrecio(Double min, Double max) {
        return guaridaRepository.findByPrecioNocheBetween(min, max);
    }

    public List<Guarida> obtenerTodosOrdenados(Sort sort) {
        return guaridaRepository.findAll(sort);
    }

    public boolean existePorNombre(String nombre) {
        return guaridaRepository.existsByNombre(nombre);
    }
}