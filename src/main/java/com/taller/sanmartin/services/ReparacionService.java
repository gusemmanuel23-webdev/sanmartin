package com.taller.sanmartin.services;

import com.taller.sanmartin.models.Reparacion;
import com.taller.sanmartin.repositories.ReparacionRepository;
import com.taller.sanmartin.repositories.VehiculoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class ReparacionService {

    private final ReparacionRepository reparacionRepository;
    private final VehiculoRepository vehiculoRepository;

    // Lista estática de estados permitidos por las reglas de negocio
    private static final List<String> ESTADOS_VALIDOS = Arrays.asList("PENDIENTE", "EN PROCESO", "FINALIZADO");

    // Inyección de dependencias por constructor
    public ReparacionService(ReparacionRepository reparacionRepository, VehiculoRepository vehiculoRepository) {
        this.reparacionRepository = reparacionRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    // 1. Obtener todas las reparaciones (Historial global)
    @Transactional(readOnly = true)
    public List<Reparacion> listarTodas() {
        return reparacionRepository.findAll();
    }

    // 2. Obtener una reparación específica por su ID
    @Transactional(readOnly = true)
    public Optional<Reparacion> buscarPorId(Long id) {
        return reparacionRepository.findById(id);
    }

    // 3. Cumplir Requerimiento: Listar reparaciones ACTIVAS (Excluye las 'Finalizado')
    @Transactional(readOnly = true)
    public List<Reparacion> listarActivas() {
        return reparacionRepository.findByEstadoNot("FINALIZADO");
    }

    // 4. Cumplir Requerimiento: Ver el historial de reparaciones de un vehículo
    @Transactional(readOnly = true)
    public List<Reparacion> listarHistorialPorVehiculo(Long vehiculoId) {
        return reparacionRepository.findByVehiculoId(vehiculoId);
    }

    // 5. Registrar una nueva orden de reparación (Ingreso a taller)
    @Transactional
    public Reparacion guardar(Reparacion reparacion) {
        // Validación 1: Descripción obligatoria
        if (reparacion.getDescripcionProblema() == null || reparacion.getDescripcionProblema().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción del problema es obligatoria al ingresar el vehículo.");
        }

        // Validación 2: El vehículo asociado debe ser real y existir en BD
        if (reparacion.getVehiculo() == null || reparacion.getVehiculo().getId() == null) {
            throw new IllegalArgumentException("La reparación debe estar vinculada a un vehículo.");
        }
        if (!vehiculoRepository.existsById(reparacion.getVehiculo().getId())) {
            throw new RuntimeException("No se puede crear la orden: El vehículo especificado no existe.");
        }

        // Automatización: Seteamos la fecha de ingreso al día de hoy si no viene establecida
        if (reparacion.getFechaIngreso() == null) {
            reparacion.setFechaIngreso(LocalDate.now());
        }

        // Automatización: Estado inicial por defecto regulado por el MVP
        if (reparacion.getEstado() == null || reparacion.getEstado().trim().isEmpty()) {
            reparacion.setEstado("Pendiente");
        } else {
            validarEstado(reparacion.getEstado());
        }

        reparacion.setDescripcionProblema(reparacion.getDescripcionProblema().trim());
        return reparacionRepository.save(reparacion);
    }

    // 6. Cumplir Requerimiento: Cambiar el estado de una reparación (Control de flujo de trabajo)
    @Transactional
    public Reparacion cambiarEstado(Long id, String nuevoEstado) {
        if (nuevoEstado == null || nuevoEstado.trim().isEmpty()) {
            throw new IllegalArgumentException("El nuevo estado no puede estar vacío.");
        }

        String estadoFormateado = nuevoEstado.trim().toUpperCase();
        validarEstado(estadoFormateado);

        return reparacionRepository.findById(id)
                .map(reparacionExistente -> {
                    // Mantenemos las minúsculas/mayúsculas según el formato visual preferido (Ej: "En Proceso")
                    if (estadoFormateado.equals("PENDIENTE")) reparacionExistente.setEstado("Pendiente");
                    if (estadoFormateado.equals("EN PROCESO")) reparacionExistente.setEstado("En Proceso");
                    if (estadoFormateado.equals("FINALIZADO")) reparacionExistente.setEstado("Finalizado");

                    return reparacionRepository.save(reparacionExistente);
                })
                .orElseThrow(() -> new RuntimeException("No se encontró la orden de reparación con el ID: " + id));
    }

    // Método utilitario interno para asegurar estados legales
    private void validarEstado(String estado) {
        String testEstado = estado.trim().toUpperCase();
        if (!ESTADOS_VALIDOS.contains(testEstado)) {
            throw new IllegalArgumentException("Estado inválido. Los valores permitidos son: Pendiente, En Proceso, Finalizado.");
        }
    }
}