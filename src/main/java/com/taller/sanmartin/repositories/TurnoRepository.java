package com.taller.sanmartin.repositories;

import com.taller.sanmartin.models.Turno;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, Long> {

    // Para cumplir el requerimiento de listar turnos según su estado (ej: "PROGRAMADO")
    List<Turno> findByEstado(String estado);
}
