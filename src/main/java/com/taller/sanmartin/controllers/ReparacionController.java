package com.taller.sanmartin.controllers;

import com.taller.sanmartin.models.Reparacion;
import com.taller.sanmartin.services.ReparacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reparaciones")
@CrossOrigin(origins = "*") // Permite conectar el Frontend de forma directa y segura sin trabas de CORS
public class ReparacionController {

    private final ReparacionService reparacionService;

    // Inyección de dependencias por constructor
    public ReparacionController(ReparacionService reparacionService) {
        this.reparacionService = reparacionService;
    }

    // 1. GET /reparaciones - Listar el historial global de todas las órdenes del taller
    @GetMapping
    public ResponseEntity<List<Reparacion>> obtenerTodas() {
        return ResponseEntity.ok(reparacionService.listarTodas());
    }

    // 2. GET /reparaciones/activas - Requerimiento: Listar órdenes en fosa excluyendo las finalizadas
    @GetMapping("/activas")
    public ResponseEntity<List<Reparacion>> obtenerActivas() {
        return ResponseEntity.ok(reparacionService.listarActivas());
    }

    // 3. GET /reparaciones/vehiculo/{vehiculoId} - Requerimiento: Traer el historial completo de un auto
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<Reparacion>> obtenerHistorialPorVehiculo(@PathVariable Long vehiculoId) {
        List<Reparacion> historial = reparacionService.listarHistorialPorVehiculo(vehiculoId);
        return ResponseEntity.ok(historial);
    }

    // 4. GET /reparaciones/{id} - Buscar una orden de reparación específica por su ID único
    @GetMapping("/{id}")
    public ResponseEntity<Reparacion> obtenerPorId(@PathVariable Long id) {
        return reparacionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404 si la orden no existe
    }

    // 5. POST /reparaciones - Registrar el ingreso de un vehículo a taller (Crea orden en 'Pendiente')
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Reparacion reparacion) {
        try {
            Reparacion nuevaReparacion = reparacionService.guardar(reparacion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaReparacion); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 por descripción vacía
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si el vehículo asignado no existe
        }
    }

    // 6. PATCH /reparaciones/{id}/estado - Control de Flujo: Cambiar el estado de un trabajo de forma ágil
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            Reparacion reparacionActualizada = reparacionService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(reparacionActualizada); // 200 OK con los cambios aplicados
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 si pasan un estado ilegal
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si la orden no existe
        }
    }
}