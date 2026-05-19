package com.taller.sanmartin.repositories;

import com.taller.sanmartin.models.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    // Permite listar de forma agrupada los servicios que correspondan a una misma área
    // Por ejemplo: traer todos los servicios de la categoría "Mantenimiento" o "Frenos"
    List<Servicio> findByCategoria(String categoria);
}