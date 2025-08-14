package com.turnomatic.backend.repository.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turnomatic.backend.model.Usuario.Rol;

public interface RolRepository extends JpaRepository<Rol, Long> {
    Rol findByNombre(String nombre);
}
