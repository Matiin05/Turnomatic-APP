package com.turnomatic.backend.service.Usuario;

import java.util.Set;

import com.turnomatic.backend.dtos.Usuario.UsuarioCreateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioUpdateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioResponse;

public interface UsuarioService {
    UsuarioResponse crearUsuario(UsuarioCreateRequest usuarioRequest, Long negocioId, Long sucursalId);

    UsuarioResponse actualizarUsuario(UsuarioUpdateRequest usuarioRequest, Long negocioId, Long sucursalId);

    Set<UsuarioResponse> listarUsuariosDeSucursal(Long negocioId, Long sucursalId);

    void eliminarUsuario(Long emailUsuario);
    
    UsuarioResponse findById(Long negocioId, Long sucursalId, Long usuarioId);
}
