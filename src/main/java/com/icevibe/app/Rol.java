package com.icevibe.app;


public enum Rol {
    ADMIN("Administrador", "Acceso total al sistema"),
    GERENTE("Gerente", "Gestión y supervisión"),
    VENDEDOR("Vendedor", "Punto de venta"),
    CLIENTE("Cliente", "Acceso básico");
    
    private final String nombre;
    private final String descripcion;
    
    Rol(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public static Rol fromString(String rol) {
        for (Rol r : Rol.values()) {
            if (r.name().equalsIgnoreCase(rol)) {
                return r;
            }
        }
        return CLIENTE; 
    }
}
