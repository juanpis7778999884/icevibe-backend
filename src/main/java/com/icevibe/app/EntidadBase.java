package com.icevibe.app;

import java.time.LocalDateTime;

/**
 * Clase abstracta base para todas las entidades
 * Cumple con requisito de herencia
 * Proporciona campos comunes para auditoría
 */
public abstract class EntidadBase {
    protected Long id;
    protected LocalDateTime fechaCreacion;
    protected LocalDateTime fechaActualizacion;
    protected boolean activo;
    
    public EntidadBase() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        this.activo = true;
    }
    
    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }
    
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public boolean isActivo() {
        return activo;
    }
    
    public void setActivo(boolean activo) {
        this.activo = activo;
    }
    
    // Método abstracto que deben implementar las clases hijas
    public abstract String obtenerIdentificador();
    
    // Método para actualizar timestamp
    public void actualizarFecha() {
        this.fechaActualizacion = LocalDateTime.now();
    }
}
