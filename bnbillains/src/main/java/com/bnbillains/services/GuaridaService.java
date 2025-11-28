package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
import com.bnbillains.repositories.GuaridaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuaridaService {

    private final GuaridaRepository guaridaRepository;

    public GuaridaService(GuaridaRepository guaridaRepository) {
        this.guaridaRepository = guaridaRepository;
    }

    public List<Guarida> obtenerTodas() {
        return guaridaRepository.findAll();
    }

    public Optional<Guarida> obtenerPorId(Long id) {
        return guaridaRepository.findById(id);
    }

    public Guarida guardar(Guarida guarida) {
        return guaridaRepository.save(guarida);
    }

    public Guarida actualizar(Long id, Guarida guarida) {
        return guaridaRepository.findById(id)
                .map(g -> {
                    g.setNombre(guarida.getNombre());
                    g.setDescripcion(guarida.getDescripcion());
                    g.setUbicacion(guarida.getUbicacion());
                    g.setPrecioNoche(guarida.getPrecioNoche());
                    g.setImagen(guarida.getImagen());
                    g.setComodidades(guarida.getComodidades());
                    g.setSalaSecreta(guarida.getSalaSecreta());
                    return guaridaRepository.save(g);
                })
                .orElseThrow(() -> new IllegalArgumentException("Guarida no encontrada"));
    }

    public void eliminar(Long id) {
        guaridaRepository.deleteById(id);
    }

    public List<Guarida> obtenerPorUbicacion(String ubicacion) {
        return guaridaRepository.findByUbicacion(ubicacion);
    }

    public List<Guarida> buscarPorNombre(String nombre) {
        return guaridaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Guarida> buscarPorRangoPrecio(Double min, Double max) {
        return guaridaRepository.findByPrecioNocheBetween(min, max);
    }

    public List<Guarida> buscarPorRangoPrecioOrdenado(Double min, Double max, Sort sort) {
        return guaridaRepository.findByPrecioNocheBetween(min, max, sort);
    }

    public List<Guarida> obtenerTodosOrdenados(Sort sort) {
        return guaridaRepository.findAll(sort);
    }

    public boolean existePorNombre(String nombre) {
        return guaridaRepository.existsByNombre(nombre);
    }
}

