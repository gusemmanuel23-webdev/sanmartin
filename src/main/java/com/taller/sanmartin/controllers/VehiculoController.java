package com.taller.sanmartin.controllers;

import com.taller.sanmartin.models.Vehiculo;
import com.taller.sanmartin.services.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/vehiculos")
@CrossOrigin(origins = "*") // Crucial para la conexión sin trabas del frontend
public class VehiculoController {

    private final VehiculoService vehiculoService;

    // Inyección del servicio por constructor
    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    // 1. GET /vehiculos - Listar todos los autos en el taller
    @GetMapping
    public ResponseEntity<List<Vehiculo>> obtenerTodos() {
        return ResponseEntity.ok(vehiculoService.listarTodos());
    }

    // 2. GET /vehiculos/{id} - Buscar auto por ID único de sistema
    @GetMapping("/{id}")
    public ResponseEntity<Vehiculo> obtenerPorId(@PathVariable Long id) {
        return vehiculoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404 si el ID no existe
    }

    // 3. GET /vehiculos/cliente/{clienteId} - Listar los autos de un cliente específico
    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Vehiculo>> obtenerPorCliente(@PathVariable Long clienteId) {
        List<Vehiculo> vehiculos = vehiculoService.buscarPorCliente(clienteId);
        return ResponseEntity.ok(vehiculos); // Si el cliente no tiene autos, devuelve lista vacía con 200 OK
    }

    // 4. POST /vehiculos - Registrar un nuevo vehículo
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Vehiculo vehiculo) {
        try {
            Vehiculo nuevoVehiculo = vehiculoService.guardar(vehiculo);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoVehiculo); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 Bad Request (Ej: Patente duplicada)
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si el Cliente ID no es real
        }
    }

    // 5. PUT /vehiculos/{id} - Actualizar datos del vehículo (marca, modelo, año, patente)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Vehiculo vehiculoActualizado) {
        try {
            Vehiculo vehiculoEditado = vehiculoService.actualizar(id, vehiculoActualizado);
            return ResponseEntity.ok(vehiculoEditado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 por patente vacía o ya tomada
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si el vehículo no existe
        }
    }

    // 6. DELETE /vehiculos/{id} - Dar de baja un auto del sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            vehiculoService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        }
    }
}