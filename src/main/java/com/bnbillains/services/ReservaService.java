package com.bnbillains.services;

import com.bnbillains.entities.Factura;
import com.bnbillains.entities.Guarida;
import com.bnbillains.entities.Reserva;
import com.bnbillains.repositories.FacturaRepository;
import com.bnbillains.repositories.GuaridaRepository;
import com.bnbillains.repositories.ReservaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    // Necesitamos acceder a las 3 tablas para orquestar la operación
    private final ReservaRepository reservaRepository;
    private final FacturaRepository facturaRepository;
    private final GuaridaRepository guaridaRepository;

    // Inyección de dependencias por Constructor
    public ReservaService(ReservaRepository reservaRepository,
                          FacturaRepository facturaRepository,
                          GuaridaRepository guaridaRepository) {
        this.reservaRepository = reservaRepository;
        this.facturaRepository = facturaRepository;
        this.guaridaRepository = guaridaRepository;
    }

    // --- LECTURA ---

    public List<Reserva> obtenerTodas(Sort sort) {
        return reservaRepository.findAll(sort);
    }

    public Optional<Reserva> obtenerPorId(Long id) {
        return reservaRepository.findById(id);
    }

    // --- LÓGICA DE NEGOCIO PRINCIPAL (CREAR) ---

    /**
     * Guarda una nueva reserva y genera automáticamente su factura correspondiente.
     * @Transactional asegura que si falla la factura, no se guarda la reserva.
     */
    @Transactional
    public Reserva guardar(Reserva reserva) {
        // 1. Validar que la fecha fin sea posterior a inicio
        validarFechas(reserva);

        // 2. Obtener la Guarida REAL de la BD para saber su PRECIO
        // (El objeto 'reserva' que viene del formulario suele traer solo el ID de la guarida)
        Guarida guaridaReal = guaridaRepository.findById(reserva.getGuarida().getId())
                .orElseThrow(() -> new IllegalArgumentException("La guarida seleccionada no existe."));

        // Asignamos la guarida completa a la reserva
        reserva.setGuarida(guaridaReal);

        // 3. Calcular el coste total (Días * PrecioNoche)
        long dias = calcularDias(reserva.getFechaInicio(), reserva.getFechaFin());
        Double costeTotal = dias * guaridaReal.getPrecioNoche();
        reserva.setCosteTotal(costeTotal);

        // 4. Guardar la Reserva en BD (Para generar su ID)
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 5. GENERAR FACTURA AUTOMÁTICA
        Factura factura = new Factura();
        factura.setFechaEmision(LocalDate.now()); // Fecha de hoy
        factura.setImporte(costeTotal);           // El precio calculado
        factura.setImpuestosMalignos(costeTotal * 0.21); // 21% de IVA
        factura.setMetodoPago("Pendiente");       // Por defecto
        factura.setReserva(reservaGuardada);      // Vinculamos a la reserva recién creada

        facturaRepository.save(factura);

        return reservaGuardada;
    }

    // --- LÓGICA DE NEGOCIO SECUNDARIA (ACTUALIZAR) ---

    /**
     * Actualiza una reserva existente y RECALCULA el importe de su factura asociada.
     */
    @Transactional
    public Reserva actualizar(Long id, Reserva reservaDatosNuevos) {
        validarFechas(reservaDatosNuevos);

        return reservaRepository.findById(id)
                .map(reservaExistente -> {
                    // A. Actualizamos datos básicos
                    reservaExistente.setFechaInicio(reservaDatosNuevos.getFechaInicio());
                    reservaExistente.setFechaFin(reservaDatosNuevos.getFechaFin());
                    reservaExistente.setEstado(reservaDatosNuevos.getEstado());
                    reservaExistente.setVillano(reservaDatosNuevos.getVillano());

                    // B. Si han cambiado la guarida, buscamos la nueva para saber el precio
                    if (!reservaExistente.getGuarida().getId().equals(reservaDatosNuevos.getGuarida().getId())) {
                        Guarida nuevaGuarida = guaridaRepository.findById(reservaDatosNuevos.getGuarida().getId())
                                .orElseThrow(() -> new IllegalArgumentException("Nueva guarida no encontrada"));
                        reservaExistente.setGuarida(nuevaGuarida);
                    }

                    // C. Recalcular Coste (por si cambiaron fechas o guarida)
                    long dias = calcularDias(reservaExistente.getFechaInicio(), reservaExistente.getFechaFin());
                    Double precioNoche = reservaExistente.getGuarida().getPrecioNoche();
                    Double nuevoCosteTotal = dias * precioNoche;

                    reservaExistente.setCosteTotal(nuevoCosteTotal);

                    // D. ACTUALIZAR LA FACTURA EXISTENTE
                    facturaRepository.findByReserva_Id(reservaExistente.getId())
                            .ifPresent(factura -> {
                                factura.setImporte(nuevoCosteTotal);
                                factura.setImpuestosMalignos(nuevoCosteTotal * 0.21);
                                facturaRepository.save(factura);
                            });

                    return reservaRepository.save(reservaExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    public void eliminar(Long id) {
        // Al borrar la reserva, la factura se borra sola por el CascadeType.ALL en la entidad
        reservaRepository.deleteById(id);
    }

    // --- MÉTODOS DE BÚSQUEDA ---

    public List<Reserva> buscarPorVillano(Long villanoId, Sort sort) {
        return reservaRepository.findByVillano_Id(villanoId, sort);
    }

    public List<Reserva> buscarPorEstado(Boolean estado, Sort sort) {
        return reservaRepository.findByEstado(estado, sort);
    }

    // --- HELPERS PRIVADOS ---

    private void validarFechas(Reserva reserva) {
        if (reserva.getFechaInicio() != null && reserva.getFechaFin() != null) {
            if (!reserva.getFechaFin().isAfter(reserva.getFechaInicio())) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior a la de inicio.");
            }
        }
    }

    private long calcularDias(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return 0;
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        return dias < 1 ? 1 : dias; // Cobramos mínimo 1 noche
    }
}