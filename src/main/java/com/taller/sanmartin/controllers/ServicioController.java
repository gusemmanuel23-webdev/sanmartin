package com.taller.sanmartin.controllers;

import com.taller.sanmartin.models.Servicio;
import com.taller.sanmartin.services.ServicioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicios")
@CrossOrigin(origins = "*") // Permite la comunicación bidireccional con el Frontend sin fallos de CORS
public class ServicioController {

    private final ServicioService servicioService;

    // Inyección de dependencias del servicio por constructor
    public ServicioController(ServicioService servicioService) {
        this.servicioService = servicioService;
    }

    // 1. GET /servicios - Obtener todo el catálogo maestro de prestaciones y precios
    @GetMapping
    public ResponseEntity<List<Servicio>> obtenerTodos() {
        return ResponseEntity.ok(servicioService.listarTodos());
    }

    // 2. GET /servicios/{id} - Buscar un servicio del catálogo por su ID único
    @GetMapping("/{id}")
    public ResponseEntity<Servicio> obtenerPorId(@PathVariable Long id) {
        return servicioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build()); // 404 si el ID no corresponde a ningún servicio
    }

    // 3. GET /servicios/categoria/{categoria} - Requerimiento: Filtrar prestaciones por rubro (Ej: "Frenos")
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<?> obtenerPorCategoria(@PathVariable String categoria) {
        try {
            List<Servicio> servicios = servicioService.buscarPorCategoria(categoria);
            return ResponseEntity.ok(servicios); // 200 OK con la lista filtrada
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 si el parámetro está vacío
        }
    }

    // 4. POST /servicios - Agregar un nuevo ítem o prestación al catálogo maestro del taller
    @PostMapping
    public ResponseEntity<?> crear(@RequestBody Servicio servicio) {
        try {
            Servicio nuevoServicio = servicioService.guardar(servicio);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoServicio); // 201 Created
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 por precio negativo o campos vacíos
        }
    }

    // 5. PUT /servicios/{id} - Actualizar datos del catálogo (Nombre, Descripción, Precio Base, Categoría)
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody Servicio servicioActualizado) {
        try {
            Servicio servicioEditado = servicioService.actualizar(id, servicioActualizado);
            return ResponseEntity.ok(servicioEditado); // 200 OK con los cambios guardados
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); // 400 por fallos de validación en los datos nuevos
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404 si el servicio no existía
        }
    }

    // 6. DELETE /servicios/{id} - Dar de baja un servicio del catálogo maestro
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            servicioService.eliminar(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // 404
        }
    }
}