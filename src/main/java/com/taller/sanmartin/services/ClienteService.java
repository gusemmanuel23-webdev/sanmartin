package com.taller.sanmartin.services;

import com.taller.sanmartin.models.Cliente;
import com.taller.sanmartin.repositories.ClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    // Inyección de dependencias por constructor (Práctica recomendada)
    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    // 1. Obtener listado de clientes
    @Transactional(readOnly = true)
    public List<Cliente> listarTodos() {
        return clienteRepository.findAll();
    }

    // 2. Obtener cliente por ID
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Long id) {
        return clienteRepository.findById(id);
    }

    // 3. Crear cliente
    @Transactional
    public Cliente guardar(Cliente cliente) {
        // Validación básica en Backend: El nombre es obligatorio según los requerimientos
        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
        }
        return clienteRepository.save(cliente);
    }

    // 4. Actualizar cliente
    @Transactional
    public Cliente actualizar(Long id, Cliente clienteActualizado) {
        return clienteRepository.findById(id)
                .map(clienteExistente -> {
                    if (clienteActualizado.getNombre() == null || clienteActualizado.getNombre().trim().isEmpty()) {
                        throw new IllegalArgumentException("El nombre del cliente es obligatorio.");
                    }
                    clienteExistente.setNombre(clienteActualizado.getNombre());
                    clienteExistente.setTelefono(clienteActualizado.getTelefono());
                    return clienteRepository.save(clienteExistente);
                })
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado con el ID: " + id));
    }

    // 5. Eliminar cliente
    @Transactional
    public void eliminar(Long id) {
        if (!clienteRepository.existsById(id)) {
            throw new RuntimeException("Cliente no encontrado con el ID: " + id);
        }
        clienteRepository.deleteById(id);
    }
}