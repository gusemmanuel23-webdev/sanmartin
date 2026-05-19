package com.taller.sanmartin.services;

import com.taller.sanmartin.models.Vehiculo;
import com.taller.sanmartin.repositories.VehiculoRepository;
import com.taller.sanmartin.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;

    // Inyección de dependencias de ambos repositorios por constructor
    public VehiculoService(VehiculoRepository vehiculoRepository, ClienteRepository clienteRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
    }

    // 1. Obtener todos los vehículos
    @Transactional(readOnly = true)
    public List<Vehiculo> listarTodos() {
        return vehiculoRepository.findAll();
    }

    // 2. Obtener vehículo por ID
    @Transactional(readOnly = true)
    public Optional<Vehiculo> buscarPorId(Long id) {
        return vehiculoRepository.findById(id);
    }

    // 3. Buscar vehículos de un cliente específico (Requerimiento Funcional)
    @Transactional(readOnly = true)
    public List<Vehiculo> buscarPorCliente(Long clienteId) {
        return vehiculoRepository.findByClienteId(clienteId);
    }

    // 4. Registrar un nuevo vehículo (Con validaciones de negocio estrictas)
    @Transactional
    public Vehiculo guardar(Vehiculo vehiculo) {
        // Validación 1: Patente obligatoria y limpia de espacios
        if (vehiculo.getPatente() == null || vehiculo.getPatente().trim().isEmpty()) {
            throw new IllegalArgumentException("La patente del vehículo es obligatoria.");
        }

        // Normalizamos la patente a mayúsculas y sin espacios para evitar trampas (ej: "abc 123" vs "ABC123")
        String patenteNormalizada = vehiculo.getPatente().trim().toUpperCase();
        vehiculo.setPatente(patenteNormalizada);

        // Validación 2: Evitar patentes duplicadas en el taller
        if (vehiculoRepository.findByPatente(patenteNormalizada).isPresent()) {
            throw new IllegalArgumentException("Ya existe un vehículo registrado con la patente: " + patenteNormalizada);
        }

        // Validación 3: El cliente/dueño debe existir obligatoriamente en la BD
        if (vehiculo.getCliente() == null || vehiculo.getCliente().getId() == null) {
            throw new IllegalArgumentException("El vehículo debe estar asociado a un cliente.");
        }

        if (!clienteRepository.existsById(vehiculo.getCliente().getId())) {
            throw new RuntimeException("No se puede registrar el vehículo: El cliente asociado no existe.");
        }

        return vehiculoRepository.save(vehiculo);
    }

    // 5. Actualizar vehículo
    @Transactional
    public Vehiculo actualizar(Long id, Vehiculo vehiculoActualizado) {
        return vehiculoRepository.findById(id)
                .map(vehiculoExistente -> {
                    if (vehiculoActualizado.getPatente() == null || vehiculoActualizado.getPatente().trim().isEmpty()) {
                        throw new IllegalArgumentException("La patente del vehículo es obligatoria.");
                    }

                    String patenteNueva = vehiculoActualizado.getPatente().trim().toUpperCase();

                    // Si intenta cambiar la patente por una que ya tiene otro auto, lo bloqueamos
                    if (!vehiculoExistente.getPatente().equals(patenteNueva) &&
                            vehiculoRepository.findByPatente(patenteNueva).isPresent()) {
                        throw new IllegalArgumentException("La patente " + patenteNueva + " ya pertenece a otro vehículo.");
                    }

                    vehiculoExistente.setPatente(patenteNueva);
                    vehiculoExistente.setMarca(vehiculoActualizado.getMarca());
                    vehiculoExistente.setModelo(vehiculoActualizado.getModelo());
                    vehiculoExistente.setAnio(vehiculoActualizado.getAnio());

                    return vehiculoRepository.save(vehiculoExistente);
                })
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con el ID: " + id));
    }

    // 6. Eliminar vehículo
    @Transactional
    public void eliminar(Long id) {
        if (!vehiculoRepository.existsById(id)) {
            throw new RuntimeException("Vehículo no encontrado con el ID: " + id);
        }
        vehiculoRepository.deleteById(id);
    }
}