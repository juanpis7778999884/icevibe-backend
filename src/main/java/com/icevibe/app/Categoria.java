package com.icevibe.app;

/**
 * Enumeración para las categorías de productos
 * Cumple con requisito de clase tipo enumeration
 */
public enum Categoria {
    BEBIDAS_ICE("Bebidas Ice", "🧊"),
    BEBIDAS_VIBE("Bebidas Vibe", "✨"),
    CERVEZAS_COCTELES("Cervezas y Cócteles", "🍺"),
    SHOTS("Shots", "🥃"),
    ADICIONALES("Adicionales", "➕"),
    GRANIZADOS_ICE("Granizados Ice", "❄️"),
    GRANIZADOS_VIBE("Granizados Vibe", "🌟"),
    GRANIZADOS_PINA_COLADA("Granizados Piña Colada", "🍹");
    
    private final String nombre;
    private final String icono;
    
    Categoria(String nombre, String icono) {
        this.nombre = nombre;
        this.icono = icono;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public String getIcono() {
        return icono;
    }
    
    public static Categoria fromString(String categoria) {
        for (Categoria c : Categoria.values()) {
            if (c.name().equalsIgnoreCase(categoria.replace(" ", "_"))) {
                return c;
            }
        }
        return ADICIONALES; // Por defecto
    }
}
