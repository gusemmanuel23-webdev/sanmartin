package com.taller.sanmartin.controllers;

import com.taller.sanmartin.models.Turno;
import com.taller.sanmartin.services.TurnoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/turnos")
@CrossOrigin(origins = "*") // Habilita la conexión del frontend sin bloqueos de seguridad
public class TurnoController {

    private final TurnoService turnoService;

    // Inyección de dependencias por constructor
    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
    }

    // 1. GET /turnos - Listar la agenda histórica y completa del taller
    @GetMapping
    public ResponseEntity<List<Turno>> obtenerTodos() {
        return ResponseEntity.ok(turnoService.listarTodos());
    }

    // 2. GET /turnos/pendientes - Requerimiento: Listar solo turnos con estado "PROGRAMADO"
    @GetMapping("/pendientes")
    public ResponseEntity<List<Turno>> obtenerPendientes() {
        return ResponseEntity.ok(turnoService.listarPendientes());
    }

    // 3. GET /turnos/{id} - Buscar un turno por su ID único
    @GetMapping("/{id}")
    public ResponseEntity<Turno> obtenerPorId(@PathVariable Long id) {
        return turnoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404 si no existe el turno
    }

    // 4. POST /turnos - Agendar un nuevo turno (Valida fecha, cliente y vehículo existentes)
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Turno turno) {
        try {
            Turno nuevoTurno = turnoService.guardar(turno);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTurno); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 por campos obligatorios vacíos
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si cliente o auto no existen
        }
    }

    // 5. PATCH /turnos/{id}/estado - Cambiar de forma ágil el estado del turno (COMPLETADO, CANCELADO)
    @PatchMapping("/{id}/estado")
    public ResponseEntity<?> cambiarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            Turno turnoCambiado = turnoService.cambiarEstado(id, nuevoEstado);
            return ResponseEntity.ok(turnoCambiado); // 200 OK con el turno actualizado
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 estado vacío
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 turno no encontrado
        }
    }

    // 6. DELETE /turnos/{id} - Cancelar/Eliminar físicamente un turno del sistema
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            turnoService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si el turno no existe
        }
    }
}