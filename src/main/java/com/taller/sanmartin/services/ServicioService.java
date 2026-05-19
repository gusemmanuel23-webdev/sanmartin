package com.taller.sanmartin.services;

import com.taller.sanmartin.models.Servicio;
import com.taller.sanmartin.repositories.ServicioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;

    // Inyección de dependencias por constructor
    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    // 1. Listar todo el catálogo de servicios
    @Transactional(readOnly = true)
    public List<Servicio> listarTodos() {
        return servicioRepository.findAll();
    }

    // 2. Buscar un servicio específico por su ID
    @Transactional(readOnly = true)
    public Optional<Servicio> buscarPorId(Long id) {
        return servicioRepository.findById(id);
    }

    // 3. Buscar servicios por categoría (Para agrupar en el Frontend)
    @Transactional(readOnly = true)
    public List<Servicio> buscarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría de búsqueda no puede estar vacía.");
        }
        return servicioRepository.findByCategoria(categoria.trim());
    }

    // 4. Registrar un nuevo servicio en el catálogo maestro
    @Transactional
    public Servicio guardar(Servicio servicio) {
        // Validación 1: Nombre obligatorio
        if (servicio.getNombre() == null || servicio.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
        }

        // Validación 2: Precio base obligatorio y no negativo
        if (servicio.getPrecioBase() == null) {
            throw new IllegalArgumentException("El precio base del servicio es obligatorio.");
        }
        if (servicio.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio base no puede ser un valor negativo.");
        }

        // Validación 3: Categoría obligatoria
        if (servicio.getCategoria() == null || servicio.getCategoria().trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría del servicio es obligatoria.");
        }

        // Limpieza de espacios innecesarios antes de persistir
        servicio.setNombre(servicio.getNombre().trim());
        servicio.setCategoria(servicio.getCategoria().trim());

        return servicioRepository.save(servicio);
    }

    // 5. Actualizar un servicio existente
    @Transactional
    public Servicio actualizar(Long id, Servicio servicioActualizado) {
        return servicioRepository.findById(id)
                .map(servicioExistente -> {
                    // Validaciones en la actualización
                    if (servicioActualizado.getNombre() == null || servicioActualizado.getNombre().trim().isEmpty()) {
                        throw new IllegalArgumentException("El nombre del servicio es obligatorio.");
                    }
                    if (servicioActualizado.getPrecioBase() == null || servicioActualizado.getPrecioBase().compareTo(BigDecimal.ZERO) < 0) {
                        throw new IllegalArgumentException("El precio base es obligatorio y debe ser mayor o igual a cero.");
                    }
                    if (servicioActualizado.getCategoria() == null || servicioActualizado.getCategoria().trim().isEmpty()) {
                        throw new IllegalArgumentException("La categoría es obligatoria.");
                    }

                    servicioExistente.setNombre(servicioActualizado.getNombre().trim());
                    servicioExistente.setDescripcion(servicioActualizado.getDescripcion());
                    servicioExistente.setPrecioBase(servicioActualizado.getPrecioBase());
                    servicioExistente.setCategoria(servicioActualizado.getCategoria().trim());

                    return servicioRepository.save(servicioExistente);
                })
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado en el catálogo con el ID: " + id));
    }

    // 6. Eliminar un servicio del catálogo
    @Transactional
    public void eliminar(Long id) {
        if (!servicioRepository.existsById(id)) {
            throw new RuntimeException("Servicio no encontrado en el catálogo con el ID: " + id);
        }
        servicioRepository.deleteById(id);
    }
}
