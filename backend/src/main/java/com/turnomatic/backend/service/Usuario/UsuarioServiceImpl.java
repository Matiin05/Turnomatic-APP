package com.turnomatic.backend.service.Usuario;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.turnomatic.backend.dtos.Usuario.UsuarioCreateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioUpdateRequest;
import com.turnomatic.backend.dtos.Usuario.UsuarioResponse;
import com.turnomatic.backend.exception.BusinessLogicException;
import com.turnomatic.backend.exception.Negocio.NegocioNotFoundException;
import com.turnomatic.backend.exception.Sucursal.SucursalNotFoundException;
import com.turnomatic.backend.exception.Usuario.UsuarioDuplicateException;
import com.turnomatic.backend.exception.Usuario.UsuarioNotFoundException;
import com.turnomatic.backend.helpers.Usuario.UsuarioMapper;
import com.turnomatic.backend.model.Negocio.Negocio;
import com.turnomatic.backend.model.Sucursal.Sucursal;
import com.turnomatic.backend.model.Usuario.Rol;
import com.turnomatic.backend.model.Usuario.Usuario;
import com.turnomatic.backend.repository.Negocio.NegocioRepository;
import com.turnomatic.backend.repository.Sucursal.SucursalRepository;
import com.turnomatic.backend.repository.Usuario.RolRepository;
import com.turnomatic.backend.repository.Usuario.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final NegocioRepository negocioRepository;
    private final SucursalRepository sucursalRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioMapper usuarioMapper;

    @Override
    public UsuarioResponse crearUsuario(UsuarioCreateRequest usuarioRequest, Long negocioId, Long sucursalId) {
        // Verificar que el negocio existe
        Negocio negocio = negocioRepository.findById(negocioId)
            .orElseThrow(() -> new NegocioNotFoundException(negocioId));
        
        // Verificar que la sucursal existe y pertenece al negocio
        Sucursal sucursal = sucursalRepository.findByIdAndNegocioId(sucursalId, negocioId)
            .orElseThrow(() -> new SucursalNotFoundException("Sucursal no encontrada con ID: " + sucursalId + " en el negocio: " + negocioId));
        
        // Verificar que el email no esté duplicado
        if (usuarioRepository.existsByEmail(usuarioRequest.getEmail())) {
            throw new UsuarioDuplicateException(usuarioRequest.getEmail(), true);
        }
        
        // Crear el usuario usando el mapper
        Usuario usuario = usuarioMapper.mapToEntity(usuarioRequest, negocio, sucursal);
        
        // Encriptar la contraseña
        usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        
        // Asignar roles según los flags
        Set<Rol> roles = new HashSet<>();
        if (usuarioRequest.isAdmin()) {
            Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN");
            if (rolAdmin == null) {
                throw new BusinessLogicException("El rol ROLE_ADMIN no existe en el sistema");
            }
            roles.add(rolAdmin);
        }
        if (usuarioRequest.isTrabajador()) {
            Rol rolTrabajador = rolRepository.findByNombre("ROLE_WORKER");
            if (rolTrabajador == null) {
                throw new BusinessLogicException("El rol ROLE_WORKER no existe en el sistema");
            }
            roles.add(rolTrabajador);
        }
        
        // Si no se especifica ningún rol, asignar ROLE_WORKER por defecto
        if (roles.isEmpty()) {
            Rol rolTrabajador = rolRepository.findByNombre("ROLE_WORKER");
            if (rolTrabajador == null) {
                throw new BusinessLogicException("El rol ROLE_WORKER no existe en el sistema");
            }
            roles.add(rolTrabajador);
        }
        
        usuario.setRoles(roles);
        
        // Guardar el usuario
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        
        // Convertir a DTO de respuesta usando el mapper
        return usuarioMapper.mapToResponse(usuarioGuardado);
    }

    @Override
    public UsuarioResponse actualizarUsuario(UsuarioUpdateRequest usuarioRequest, Long negocioId, Long sucursalId) {
        // Verificar que el negocio existe
        negocioRepository.findById(negocioId)
            .orElseThrow(() -> new NegocioNotFoundException(negocioId));
        
        // Verificar que la sucursal existe y pertenece al negocio
        Sucursal sucursal = sucursalRepository.findByIdAndNegocioId(sucursalId, negocioId)
            .orElseThrow(() -> new SucursalNotFoundException("Sucursal no encontrada con ID: " + sucursalId + " en el negocio: " + negocioId));
        
        // Buscar el usuario por email
        Usuario usuario = usuarioRepository.findByEmail(usuarioRequest.getEmail())
            .orElseThrow(() -> new UsuarioNotFoundException(usuarioRequest.getEmail(), true));
        
        // Verificar que el usuario pertenece al negocio especificado (no se puede cambiar de negocio)
        if (!usuario.getNegocio().getId().equals(negocioId)) {
            throw new BusinessLogicException("No se puede cambiar el negocio de un usuario. El usuario pertenece al negocio: " + usuario.getNegocio().getId());
        }
        
        // Verificar que la nueva sucursal pertenece al mismo negocio del usuario
        if (!sucursal.getNegocio().getId().equals(usuario.getNegocio().getId())) {
            throw new BusinessLogicException("La sucursal no pertenece al negocio del usuario");
        }
        
        // Verificar si realmente hay cambios antes de actualizar
        boolean hayCambios = false;
        
        // Verificar cambios en campos básicos
        if ((usuarioRequest.getNombre() != null && !usuarioRequest.getNombre().equals(usuario.getNombre())) ||
            (usuarioRequest.getApellido() != null && !usuarioRequest.getApellido().equals(usuario.getApellido())) ||
            (usuarioRequest.getEmail() != null && !usuarioRequest.getEmail().equals(usuario.getEmail()))) {
            hayCambios = true;
        }
        
        // Verificar cambio de contraseña
        if (usuarioRequest.getPassword() != null && !usuarioRequest.getPassword().isEmpty()) {
            hayCambios = true;
        }
        
        // Verificar cambio de sucursal
        if (!usuario.getSucursal().getId().equals(sucursalId)) {
            hayCambios = true;
        }
        
        // Verificar cambios en roles
        boolean esAdmin = usuarioRequest.isAdmin();
        boolean esTrabajador = usuarioRequest.isTrabajador();
        boolean tieneRolAdmin = usuario.getRoles().stream().anyMatch(rol -> "ROLE_ADMIN".equals(rol.getNombre()));
        boolean tieneRolTrabajador = usuario.getRoles().stream().anyMatch(rol -> "ROLE_WORKER".equals(rol.getNombre()));
        
        if (esAdmin != tieneRolAdmin || esTrabajador != tieneRolTrabajador) {
            hayCambios = true;
        }
        
        // Si no hay cambios, retornar el usuario actual sin modificar
        if (!hayCambios) {
            return usuarioMapper.mapToResponse(usuario);
        }
        
        // Actualizar los campos del usuario usando el mapper (excepto negocio)
        usuarioMapper.updateEntity(usuarioRequest, usuario);
        
        // Actualizar la sucursal (permitido)
        usuario.setSucursal(sucursal);
        
        // Si se cambió la contraseña, encriptarla
        if (usuarioRequest.getPassword() != null && !usuarioRequest.getPassword().isEmpty()) {
            usuario.setPassword(passwordEncoder.encode(usuarioRequest.getPassword()));
        }
        
        // Actualizar roles según los flags
        Set<Rol> roles = new HashSet<>();
        if (usuarioRequest.isAdmin()) {
            Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMIN");
            if (rolAdmin == null) {
                throw new BusinessLogicException("El rol ROLE_ADMIN no existe en el sistema");
            }
            roles.add(rolAdmin);
        }
        if (usuarioRequest.isTrabajador()) {
            Rol rolTrabajador = rolRepository.findByNombre("ROLE_WORKER");
            if (rolTrabajador == null) {
                throw new BusinessLogicException("El rol ROLE_WORKER no existe en el sistema");
            }
            roles.add(rolTrabajador);
        }
        
        // Si no se especificó ningún rol, mantener ROLE_WORKER por defecto
        if (roles.isEmpty()) {
            Rol rolTrabajador = rolRepository.findByNombre("ROLE_WORKER");
            if (rolTrabajador == null) {
                throw new BusinessLogicException("El rol ROLE_WORKER no existe en el sistema");
            }
            roles.add(rolTrabajador);
        }
        
        usuario.setRoles(roles);
        
        // Guardar el usuario actualizado
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        
        // Convertir a DTO de respuesta usando el mapper
        return usuarioMapper.mapToResponse(usuarioActualizado);
    }

    @Override
    public Set<UsuarioResponse> listarUsuariosDeSucursal(Long negocioId, Long sucursalId) {
        // Verificar que el negocio existe
        negocioRepository.findById(negocioId)
            .orElseThrow(() -> new NegocioNotFoundException(negocioId));
        
        // Verificar que la sucursal existe y pertenece al negocio
        sucursalRepository.findByIdAndNegocioId(sucursalId, negocioId)
            .orElseThrow(() -> new SucursalNotFoundException("Sucursal no encontrada con ID: " + sucursalId + " en el negocio: " + negocioId));
        
        // Buscar todos los usuarios de la sucursal
        Set<Usuario> usuarios = usuarioRepository.findBySucursalIdAndNegocioId(sucursalId, negocioId);
        
        // Convertir a DTOs usando el mapper
        return usuarioMapper.mapToSetOfResponse(usuarios);
    }

    @Override
    public void eliminarUsuario(Long idUsuario) {
        // Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findById(idUsuario)
            .orElseThrow(() -> new UsuarioNotFoundException(idUsuario));
        
        // Eliminar el usuario
        usuarioRepository.delete(usuario);
    }
    
    @Override
    public UsuarioResponse findById(Long negocioId, Long sucursalId, Long usuarioId) {
        // Verificar que el negocio existe
        negocioRepository.findById(negocioId)
            .orElseThrow(() -> new NegocioNotFoundException(negocioId));
        
        // Verificar que la sucursal existe y pertenece al negocio
        sucursalRepository.findByIdAndNegocioId(sucursalId, negocioId)
            .orElseThrow(() -> new SucursalNotFoundException("Sucursal no encontrada con ID: " + sucursalId + " en el negocio: " + negocioId));
        
        // Buscar el usuario por ID
        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new UsuarioNotFoundException(usuarioId));
        
        // Verificar que el usuario pertenece a la sucursal y negocio especificados
        if (!usuario.getSucursal().getId().equals(sucursalId) || !usuario.getNegocio().getId().equals(negocioId)) {
            throw new BusinessLogicException("El usuario no pertenece a la sucursal y negocio especificados");
        }
        
        // Convertir a DTO de respuesta usando el mapper
        return usuarioMapper.mapToResponse(usuario);
    }
}
