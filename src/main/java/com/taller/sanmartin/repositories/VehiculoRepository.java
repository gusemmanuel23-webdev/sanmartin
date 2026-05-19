package com.taller.sanmartin.repositories;

import com.taller.sanmartin.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {

    // 1. Para validar que no se registren patentes duplicadas antes de guardar
    Optional<Vehiculo> findByPatente(String patente);

    // 2. Para cumplir el requerimiento de visualizar los vehículos de un cliente específico
    List<Vehiculo> findByClienteId(Long clienteId);
}