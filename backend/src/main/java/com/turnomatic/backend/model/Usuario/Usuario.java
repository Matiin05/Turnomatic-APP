package com.turnomatic.backend.model.Usuario;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.turnomatic.backend.model.Negocio.Negocio;
import com.turnomatic.backend.model.Sucursal.Sucursal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuarios")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Datos para el login

    @Column(unique = true)
    @Email
    private String email;
    private String password;
    // Authorities (Los roles que tiene el usuario)

    @ManyToMany(fetch = FetchType.EAGER) // EAGER porque casi siempre necesitarás los roles al autenticar
    @JoinTable(name = "usuario_roles", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "rol_id"))
    private Set<Rol> roles;

    // Datos del usuario (Los usuarios son o admin o trabajadores, ambos dos
    // pertenecen a una sola sucursal)
    // Personales
    @Size(min = 3)
    @NotBlank
    private String nombre;

    @Size(min = 2)
    @NotBlank
    private String apellido;

    // Datos de negocio
    @ManyToOne
    @JoinColumn(name = "negocio_id", nullable = false)
    @NotNull
    private Negocio negocio;

    @ManyToOne
    @JoinColumn(name = "sucursal_id", nullable = false)
    @NotNull
    private Sucursal sucursal;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convertir los roles a GrantedAuthority
        return roles.stream()
                .map(rol -> new org.springframework.security.core.authority.SimpleGrantedAuthority(rol.getNombre()))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
