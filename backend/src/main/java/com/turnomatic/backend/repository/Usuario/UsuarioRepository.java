package com.turnomatic.backend.repository.Usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import com.turnomatic.backend.model.Usuario.Usuario;

import java.util.List;
import java.util.Optional;
import com.turnomatic.backend.model.Usuario.Rol;
import java.util.Set;



public interface UsuarioRepository extends JpaRepository<Usuario,Long>{
    Optional<Usuario> findByEmail(String email);
    
    List<Usuario> findByRoles(Set<Rol> roles);

    List<Usuario> findBySucursalId(Long sucursalId);
}
