package com.icevibe.app;

/**
 * Clase Usuario que extiende de EntidadBase
 * Cumple con requisito de herencia
 */
public class Usuario extends EntidadBase {
    private String codigo;
    private String nombre;
    private String email;
    private String password;
    private Rol rol;
    
    public Usuario() {
        super();
    }
    
    @Override
    public String obtenerIdentificador() {
        return codigo + " - " + nombre;
    }
    
    // Getters y Setters
    public String getCodigo() {
        return codigo;
    }
    
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public Rol getRol() {
        return rol;
    }
    
    public void setRol(String rol) {
        this.rol = Rol.fromString(rol);
    }
    
    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
