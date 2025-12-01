package com.bnbillains.services;

import com.bnbillains.entities.Guarida;
import com.bnbillains.repositories.GuaridaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GuaridaService {

    //Accedemos al repositorio
    private final GuaridaRepository guaridaRepository;

    public GuaridaService(GuaridaRepository guaridaRepository) {
        this.guaridaRepository = guaridaRepository;
    }

    //Obtenemos todas las guaridas
    public List<Guarida> obtenerTodas() {
        return guaridaRepository.findAll();
    }

    //Devuelve un optional porque puede que la guarida no exista
    public Optional<Guarida> obtenerPorId(Long id) {
        return guaridaRepository.findById(id);
    }

    //Guardamos la guarida
    //Deberíamos de añadir si el precio es <0 = excepcion
    public Guarida guardar(Guarida guarida) {
        return guaridaRepository.save(guarida);
    }


    public Guarida actualizar(Long id, Guarida guarida) {
        //Buscamos si existe la original
        return guaridaRepository.findById(id)
                .map(g -> {
                    //si existe, actualizamos los campos con los datos nuevos
                    g.setNombre(guarida.getNombre());
                    g.setDescripcion(guarida.getDescripcion());
                    g.setUbicacion(guarida.getUbicacion());
                    g.setPrecioNoche(guarida.getPrecioNoche());
                    g.setImagen(guarida.getImagen());
                    g.setComodidades(guarida.getComodidades());
                    g.setSalaSecreta(guarida.getSalaSecreta());
                    //guardamos la guarida modificada
                    return guaridaRepository.save(g);
                })
                //si no existe, lanzamos error
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

    //
    public List<Guarida> buscarPorRangoPrecioOrdenado(Double min, Double max, Sort sort) {
        return guaridaRepository.findByPrecioNocheBetween(min, max, sort);
    }

    //metodo puente para obtener todo ordenado
    public List<Guarida> obtenerTodosOrdenados(Sort sort) {
        return guaridaRepository.findAll(sort);
    }

    public boolean existePorNombre(String nombre) {
        return guaridaRepository.existsByNombre(nombre);
    }
}

