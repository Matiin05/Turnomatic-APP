package com.turnomatic.backend.model.Negocio;

import java.time.OffsetDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.URL;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(
    name = "negocios",
    uniqueConstraints = {
        // Si más adelante sumás multi-tenant, reemplazá por (tenant_id, nombre)
        @UniqueConstraint(name = "uk_negocios_nombre", columnNames = {"nombre"})
    }
)
@Data
public class Negocio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


      // Requerido, largo máximo y único
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 30, message = "El nombre debe tener entre {min} y {max} caracteres")
    @Pattern(regexp = "^[a-zA-ZÀ-ÿ0-9\\s]+$", message = "El nombre solo puede contener letras, números y espacios")
    @Column(name = "nombre", unique = true, nullable = false, length = 120)
    private String nombre;

    @NotBlank(message = "La descripción es obligatoria")
    @Size(max = 150, message = "La descripción no puede superar {max} caracteres")
    @Column(name = "descripcion", nullable = false, length = 300)
    private String descripcion;

    @NotBlank(message = "El logo es obligatorio")
    @Size(max = 255, message = "El logo no puede superar {max} caracteres")
    @URL(message = "El logo debe ser una URL válida")
    @Column(name = "logo", nullable = false, length = 255)
    private String logo;

      // Timestamps automáticos
      @CreationTimestamp
      @Column(name = "created_at", updatable = false, nullable = false)
      private OffsetDateTime createdAt;
  
      @UpdateTimestamp
      @Column(name = "updated_at")
      private OffsetDateTime updatedAt;
  
}
