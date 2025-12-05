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

    // --- MÉTODOS DE LECTURA (Sin cambios) ---
    public List<Reserva> obtenerTodas(Sort sort) { return reservaRepository.findAll(sort); }
    public Optional<Reserva> obtenerPorId(Long id) { return reservaRepository.findById(id); }

    // --- GUARDAR CON VALIDACIÓN DE FECHAS OCUPADAS ---
    @Transactional
    public Reserva guardar(Reserva reserva) {
        // 1. Validar lógica de fechas (Fin > Inicio)
        validarFechasLogicas(reserva);

        // 2.VALIDAR DISPONIBILIDAD (EL CAMBIO IMPORTANTE)
        List<Reserva> conflictos = reservaRepository.encontrarConflictos(
                reserva.getGuarida().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin()
        );

        if (!conflictos.isEmpty()) {
            // Cogemos la primera reserva que estorba para chivarnos de las fechas
            Reserva ocupante = conflictos.get(0);


            throw new IllegalArgumentException(
                    "¡Conflicto de Agendas! La guarida '" + ocupante.getGuarida().getNombre() +
                            "' ya está reservada por otro villano desde el " +
                            ocupante.getFechaInicio() + " hasta el " + ocupante.getFechaFin() +
                            ". Busca fechas libres."
            );
        }

        // 3. Recuperar Guarida Real (Precio)
        Guarida guaridaReal = guaridaRepository.findById(reserva.getGuarida().getId())
                .orElseThrow(() -> new IllegalArgumentException("Guarida no existe"));
        reserva.setGuarida(guaridaReal);

        // 4. Calcular Coste y Guardar
        long dias = calcularDias(reserva.getFechaInicio(), reserva.getFechaFin());
        Double costeTotal = dias * guaridaReal.getPrecioNoche();
        reserva.setCosteTotal(costeTotal);

        Reserva reservaGuardada = reservaRepository.save(reserva);

        // 5. Factura Automática
        crearFacturaAutomatica(reservaGuardada, costeTotal);

        return reservaGuardada;
    }

    // --- ACTUALIZAR CON VALIDACIÓN DE FECHAS OCUPADAS ---
    @Transactional
    public Reserva actualizar(Long id, Reserva reservaDatos) {
        validarFechasLogicas(reservaDatos);

        return reservaRepository.findById(id)
                .map(reservaExistente -> {

                    // 1. 🔥 VALIDAR DISPONIBILIDAD (Excluyendo la propia reserva)
                    // Si cambio las fechas, tengo que ver que no choque con OTROS, pero ignorándome a mí mismo.
                    List<Reserva> conflictos = reservaRepository.encontrarConflictosParaActualizar(
                            reservaDatos.getGuarida().getId(), // Ojo, si cambia la guarida, validar la nueva
                            id, // Mi ID para excluirme
                            reservaDatos.getFechaInicio(),
                            reservaDatos.getFechaFin()
                    );

                    if (!conflictos.isEmpty()) {
                        throw new IllegalArgumentException("Fechas no disponibles. Coinciden con otra reserva existente.");
                    }

                    // 2. Actualizar datos
                    reservaExistente.setFechaInicio(reservaDatos.getFechaInicio());
                    reservaExistente.setFechaFin(reservaDatos.getFechaFin());
                    reservaExistente.setEstado(reservaDatos.getEstado());
                    reservaExistente.setVillano(reservaDatos.getVillano());

                    // (Gestión de cambio de guarida omitida por brevedad, asumo que se mantiene o se gestiona igual)
                    if (!reservaExistente.getGuarida().getId().equals(reservaDatos.getGuarida().getId())) {
                        Guarida nuevaGuarida = guaridaRepository.findById(reservaDatos.getGuarida().getId()).orElseThrow();
                        reservaExistente.setGuarida(nuevaGuarida);
                    }

                    // 3. Recalcular Coste y Factura
                    long dias = calcularDias(reservaExistente.getFechaInicio(), reservaExistente.getFechaFin());
                    Double nuevoCoste = dias * reservaExistente.getGuarida().getPrecioNoche();
                    reservaExistente.setCosteTotal(nuevoCoste);

                    actualizarFactura(reservaExistente, nuevoCoste);

                    return reservaRepository.save(reservaExistente);
                })
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }

    // ... (delete, búsquedas, etc... IGUAL QUE ANTES) ...
    public void eliminar(Long id) { reservaRepository.deleteById(id); }
    public List<Reserva> buscarPorVillano(Long vid, Sort s) { return reservaRepository.findByVillano_Id(vid, s); }
    public List<Reserva> buscarPorEstado(Boolean e, Sort s) { return reservaRepository.findByEstado(e, s); }

    // --- HELPERS PRIVADOS ---

    private void validarFechasLogicas(Reserva r) {
        if (r.getFechaInicio() != null && r.getFechaFin() != null) {
            if (!r.getFechaFin().isAfter(r.getFechaInicio())) { // Fin debe ser > Inicio
                throw new IllegalArgumentException("La fecha de fin debe ser posterior al inicio.");
            }
        }
    }

    private long calcularDias(LocalDate inicio, LocalDate fin) {
        if (inicio == null || fin == null) return 0;
        long dias = ChronoUnit.DAYS.between(inicio, fin);
        return dias < 1 ? 1 : dias;
    }

    private void crearFacturaAutomatica(Reserva reserva, Double importe) {
        Factura f = new Factura();
        f.setFechaEmision(LocalDate.now());
        f.setImporte(importe);
        f.setImpuestosMalignos(importe * 0.21);
        f.setMetodoPago("Pendiente");
        f.setReserva(reserva);
        facturaRepository.save(f);
    }

    private void actualizarFactura(Reserva reserva, Double nuevoImporte) {
        facturaRepository.findByReserva_Id(reserva.getId()).ifPresent(f -> {
            f.setImporte(nuevoImporte);
            f.setImpuestosMalignos(nuevoImporte * 0.21);
            facturaRepository.save(f);
        });
    }

    public List<Reserva> buscarPorGuarida(Long guaridaId, Sort sort) {
        // Protección: Si el controlador nos pasa 'null' en el sort (como hace el calendario),
        // usamos Sort.unsorted() para que JPA no se queje.
        if (sort == null) {
            sort = Sort.unsorted();
        }
        return reservaRepository.findByGuarida_Id(guaridaId, sort);
    }
}