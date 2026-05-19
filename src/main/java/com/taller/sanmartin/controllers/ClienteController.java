package com.taller.sanmartin.controllers;

import com.taller.sanmartin.models.Cliente;
import com.taller.sanmartin.services.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "*") // Permite que el frontend se conecte sin trabas de CORS
public class ClienteController {

    private final ClienteService clienteService;

    // Inyección del servicio por constructor
    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    // 1. GET /clientes - Obtener listado completo
    @GetMapping
    public ResponseEntity<List<Cliente>> obtenerTodos() {
        List<Cliente> clientes = clienteService.listarTodos();
        return ResponseEntity.ok(clientes); // Devuelve 200 OK con la lista
    }

    // 2. GET /clientes/{id} - Obtener cliente por ID
    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtenerPorId(@PathVariable Long id) {
        return clienteService.buscarPorId(id)
                .map(cliente -> ResponseEntity.ok(cliente)) // 200 OK si existe
                .orElse(ResponseEntity.notFound().build()); // 404 Not Found si no existe
    }

    // 3. POST /clientes - Crear un cliente nuevo
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Cliente cliente) {
        try {
            Cliente nuevoCliente = clienteService.guardar(cliente);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoCliente); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request si falla validación
        }
    }

    // 4. PUT /clientes/{id} - Actualizar datos de un cliente
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Cliente clienteActualizado) {
        try {
            Cliente clienteEditado = clienteService.actualizar(id, clienteActualizado);
            return ResponseEntity.ok(clienteEditado); // 200 OK con el cliente modificado
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        }
    }

    // 5. DELETE /clientes/{id} - Eliminar un cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            clienteService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content (eliminación exitosa sin cuerpo)
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 Not Found
        }
    }
}
