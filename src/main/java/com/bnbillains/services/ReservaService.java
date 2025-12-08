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

    private final ReservaRepository reservaRepository;
    private final FacturaRepository facturaRepository;
    private final GuaridaRepository guaridaRepository;

    public ReservaService(ReservaRepository reservaRepository,
                          FacturaRepository facturaRepository,
                          GuaridaRepository guaridaRepository) {
        this.reservaRepository = reservaRepository;
        this.facturaRepository = facturaRepository;
        this.guaridaRepository = guaridaRepository;
    }

    // --- MÉTODOS DE LECTURA ---
    public List<Reserva> obtenerTodas(Sort sort) { return reservaRepository.findAll(sort); }
    public Optional<Reserva> obtenerPorId(Long id) { return reservaRepository.findById(id); }

    // --- CREACIÓN DE RESERVA (El corazón del negocio) ---
    @Transactional // Si falla la factura, se deshace la reserva (todo o nada)
    public Reserva guardar(Reserva reserva) {
        // 1. Validamos que la fecha fin sea posterior al inicio
        validarFechasLogicas(reserva);

        // 2. DETECCIÓN DE OVERBOOKING
        // Preguntamos a la base de datos si hay alguien en esa guarida en esas fechas
        List<Reserva> conflictos = reservaRepository.encontrarConflictos(
                reserva.getGuarida().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin()
        );

        // Si hay conflictos, bloqueamos la operación lanzando un error
        if (!conflictos.isEmpty()) {
            throw new IllegalArgumentException("¡Imposible! La guarida está ocupada en esas fechas.");
        }

        // 3. Recuperamos la guarida para saber el precio real por noche
        Guarida guaridaReal = guaridaRepository.findById(reserva.getGuarida().getId())
                .orElseThrow(() -> new IllegalArgumentException("Guarida no existe"));
        reserva.setGuarida(guaridaReal);

        // 4. LÓGICA DE NEGOCIO: Calculamos el precio total nosotros mismos
        long dias = calcularDias(reserva.getFechaInicio(), reserva.getFechaFin());
        Double costeTotal = dias * guaridaReal.getPrecioNoche();
        reserva.setCosteTotal(costeTotal);

        // 5. Guardamos la reserva en BD
        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 6. GENERACIÓN AUTOMÁTICA DE FACTURA
        // Inmediatamente después de reservar, creamos la factura pendiente de cobro
        crearFacturaAutomatica(reservaGuardada, costeTotal);

        return reservaGuardada;
    }

    // --- EDICIÓN DE RESERVA ---
    @Transactional
    public Reserva actualizar(Long id, Reserva reservaDatos) {
        validarFechasLogicas(reservaDatos);

        return reservaRepository.findById(id)
                .map(reservaExistente -> {

                    // 1. Validación de fechas EXCLUYENDO la propia reserva actual
                    // (Para que no choque consigo misma al comprobar disponibilidad)
                    List<Reserva> conflictos = reservaRepository.encontrarConflictosParaActualizar(
                            reservaDatos.getGuarida().getId(),
                            id,
                            reservaDatos.getFechaInicio(),
                            reservaDatos.getFechaFin()
                    );

                    if (!conflictos.isEmpty()) {
                        throw new IllegalArgumentException("Fechas no disponibles. Coinciden con otra reserva.");
                    }

                    // 2. Actualizamos los datos
                    reservaExistente.setFechaInicio(reservaDatos.getFechaInicio());
                    reservaExistente.setFechaFin(reservaDatos.getFechaFin());
                    reservaExistente.setEstado(reservaDatos.getEstado());
                    reservaExistente.setVillano(reservaDatos.getVillano());

                    // Si cambia la guarida, actualizamos la referencia
                    if (!reservaExistente.getGuarida().getId().equals(reservaDatos.getGuarida().getId())) {
                        Guarida nuevaGuarida = guaridaRepository.findById(reservaDatos.getGuarida().getId()).orElseThrow();
                        reservaExistente.setGuarida(nuevaGuarida);
                    }

                    // 3. RECALCULAR COSTES (importante si han cambiado las fechas)
                    long dias = calcularDias(reservaExistente.getFechaInicio(), reservaExistente.getFechaFin());
                    Double nuevoCoste = dias * reservaExistente.getGuarida().getPrecioNoche();
                    reservaExistente.setCosteTotal(nuevoCoste);

                    // 4. Sincronizamos la factura con el nuevo precio
                    actualizarFactura(reservaExistente, nuevoCoste);

                    return reservaRepository.save(reservaExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    public void eliminar(Long id) { reservaRepository.deleteById(id); }
    public List<Reserva> buscarPorVillano(Long vid, Sort s) { return reservaRepository.findByVillano_Id(vid, s); }
    public List<Reserva> buscarPorEstado(Boolean e, Sort s) { return reservaRepository.findByEstado(e, s); }

    // --- MÉTODOS PRIVADOS (Helpers) ---

    private void validarFechasLogicas(Reserva r) {
        if (r.getFechaInicio() != null && r.getFechaFin() != null) {
            if (!r.getFechaFin().isAfter(r.getFechaInicio())) {
                throw new IllegalArgumentException("La fecha de fin debe ser posterior al inicio.");
            }
        }
    }

    private long calcularDias(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return 0;
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        return dias < 1 ? 1 : dias; // Cobramos mínimo 1 día
    }

    // Crea la factura inicial vinculada a la reserva
    private void crearFacturaAutomatica(Reserva reserva, Double importe) {
        Factura f = new Factura();
        f.setFechaEmision(LocalDate.now());
        f.setImporte(importe);
        f.setImpuestosMalignos(importe * 0.21); // 21% de IVA Maligno
        f.setMetodoPago("Pendiente");
        f.setReserva(reserva);
        facturaRepository.save(f);
    }

    // Actualiza el importe de la factura si la reserva cambia
    private void actualizarFactura(Reserva reserva, Double nuevoImporte) {
        facturaRepository.findByReserva_Id(reserva.getId()).ifPresent(f -> {
            f.setImporte(nuevoImporte);
            f.setImpuestosMalignos(nuevoImporte * 0.21);
            facturaRepository.save(f);
        });
    }
}