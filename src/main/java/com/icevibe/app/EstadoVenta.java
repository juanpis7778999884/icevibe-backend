package com.icevibe.app;

/**
 * Enumeración para los estados de una venta
 * Cumple con requisito de clase tipo enumeration
 */
public enum EstadoVenta {
    COMPLETADA("Completada", "#10b981"),
    PENDIENTE("Pendiente", "#f59e0b"),
    CANCELADA("Cancelada", "#ef4444");
    
    private final String nombre;
    private final String color;
    
    EstadoVenta(String nombre, String color) {
        this.nombre = nombre;
        this.color = color;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getColor() {
        return color;
    }
    
    public static EstadoVenta fromString(String estado) {
        for (EstadoVenta e : EstadoVenta.values()) {
            if (e.name().equalsIgnoreCase(estado)) {
                return e;
            }
        }
        return PENDIENTE; // Por defecto
    }
}
