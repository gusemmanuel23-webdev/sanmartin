package com.taller.sanmartin.repositories;

import com.taller.sanmartin.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // Al heredar de JpaRepository, Spring Data JPA genera automáticamente
    // todos los métodos CRUD básicos (save, findById, findAll, deleteById).
}