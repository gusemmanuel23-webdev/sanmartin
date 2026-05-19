package com.taller.sanmartin.repositories;

import com.taller.sanmartin.models.Reparacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReparacionRepository extends JpaRepository<Reparacion, Long> {

    // 1. Para cumplir el requerimiento de listar el historial de reparaciones de un auto específico
    List<Reparacion> findByVehiculoId(Long vehiculoId);

    // 2. Para cumplir el requerimiento de la Visualización General: identificar trabajos por estado
    // Permite traer por separado las "Pendiente", "En Proceso" o "Finalizado"
    List<Reparacion> findByEstado(String estado);

    // 3. Consulta avanzada derivada: Permite listar todas las reparaciones activas excluyendo las terminadas
    List<Reparacion> findByEstadoNot(String estado);
}