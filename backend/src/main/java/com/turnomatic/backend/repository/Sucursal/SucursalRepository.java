package com.turnomatic.backend.repository.Sucursal;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turnomatic.backend.model.Sucursal.Sucursal;

public interface SucursalRepository extends JpaRepository<Sucursal,Long>{

    boolean existsByNombreAndNegocioId(String nombre , Long negocioId);

    boolean existsByIdAndNegocioId(Long sucursalId, Long negocioId);

    long deleteByIdAndNegocioId(Long id, Long negocioId);

    Set<Sucursal> findAllByNegocioId(Long negocioId);
    
    java.util.Optional<Sucursal> findByIdAndNegocioId(Long sucursalId, Long negocioId);
}
