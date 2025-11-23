package com.icevibe.app;

import java.math.BigDecimal;

/**
 * Clase Producto que extiende de EntidadBase
 * Cumple con requisito de herencia
 */
public class Producto extends EntidadBase {
    private String codigo;
    private String nombre;
    private Categoria categoria;
    private BigDecimal precio;
    private String descripcion;
    private int stock;
    private int stockMinimo;
    
    public Producto() {
        super();
        this.stock = 0;
        this.stockMinimo = 5;
    }
    
    @Override
    public String obtenerIdentificador() {
        return codigo + " - " + nombre;
    }
    
    public boolean tieneStockBajo() {
        return stock <= stockMinimo;
    }
    
    public boolean tieneStock(int cantidad) {
        return stock >= cantidad;
    }
    
    public void reducirStock(int cantidad) {
        if (tieneStock(cantidad)) {
            this.stock -= cantidad;
            actualizarFecha();
        }
    }
    
    public void aumentarStock(int cantidad) {
        this.stock += cantidad;
        actualizarFecha();
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
    
    public Categoria getCategoria() {
        return categoria;
    }
    
    public void setCategoria(String categoria) {
        this.categoria = Categoria.fromString(categoria);
    }
    
    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }
    
    public BigDecimal getPrecio() {
        return precio;
    }
    
    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public int getStock() {
        return stock;
    }
    
    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public int getStockMinimo() {
        return stockMinimo;
    }
    
    public void setStockMinimo(int stockMinimo) {
        this.stockMinimo = stockMinimo;
    }
}
