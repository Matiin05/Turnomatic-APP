package com.turnomatic.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turnomatic.backend.model.Negocio;

public interface NegocioRepository extends JpaRepository<Negocio, Long>{
    Boolean existsByNombre(String nombre);
}
