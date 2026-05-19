package com.taller.sanmartin.services;

import com.taller.sanmartin.models.Turno;
import com.taller.sanmartin.repositories.TurnoRepository;
import com.taller.sanmartin.repositories.ClienteRepository;
import com.taller.sanmartin.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class TurnoService {

    private final TurnoRepository turnoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;

    // Inyección de todas las dependencias necesarias por constructor
    public TurnoService(TurnoRepository turnoRepository,
                        ClienteRepository clienteRepository,
                        VehiculoRepository vehiculoRepository) {
        this.turnoRepository = turnoRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    // 1. Obtener todos los turnos registrados
    @Transactional(readOnly = true)
    public List<Turno> listarTodos() {
        return turnoRepository.findAll();
    }

    // 2. Obtener turno por ID
    @Transactional(readOnly = true)
    public Optional<Turno> buscarPorId(Long id) {
        return turnoRepository.findById(id);
    }

    // 3. Cumplir Requerimiento: Mostrar turnos pendientes (PROGRAMADO)
    @Transactional(readOnly = true)
    public List<Turno> listarPendientes() {
        return turnoRepository.findByEstado("PROGRAMADO");
    }

    // 4. Registrar un nuevo turno con validaciones de integridad
    @Transactional
    public Turno guardar(Turno turno) {
        // Validación 1: Fecha obligatoria
        if (turno.getFechaTurno() == null) {
            throw new IllegalArgumentException("La fecha y hora del turno es obligatoria.");
        }

        // Validación 2: El cliente debe existir en la base de datos
        if (turno.getCliente() == null || turno.getCliente().getId() == null) {
            throw new IllegalArgumentException("El turno debe estar asociado a un cliente.");
        }
        if (!clienteRepository.existsById(turno.getCliente().getId())) {
            throw new RuntimeException("No se puede agendar: El cliente especificado no existe.");
        }

        // Validación 3: El vehículo debe existir en la base de datos
        if (turno.getVehiculo() == null || turno.getVehiculo().getId() == null) {
            throw new IllegalArgumentException("El turno debe estar asociado a un vehículo.");
        }
        if (!vehiculoRepository.existsById(turno.getVehiculo().getId())) {
            throw new RuntimeException("No se puede agendar: El vehículo especificado no existe.");
        }

        // Forzar estado inicial reglamentado si viene vacío
        if (turno.getEstado() == null || turno.getEstado().trim().isEmpty()) {
            turno.setEstado("PROGRAMADO");
        }

        return turnoRepository.save(turno);
    }

    // 5. Cambiar el estado de un turno (ej: COMPLETADO o CANCELADO)
    @Transactional
    public Turno cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo estado no puede estar vacío.");
        }

        String estadoFormateado = nuevoEstado.trim().toUpperCase();

        return turnoRepository.findById(id)
                .map(turnoExistente -> {
                    turnoExistente.setEstado(estadoFormateado);
                    return turnoRepository.save(turnoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Turno no encontrado con el ID: " + id));
    }

    // 6. Eliminar un turno
    @Transactional
    public void eliminar(Long id) {
        if (!turnoRepository.existsById(id)) {
            throw new RuntimeException("Turno no encontrado con el ID: " + id);
        }
        turnoRepository.deleteById(id);
    }
}
