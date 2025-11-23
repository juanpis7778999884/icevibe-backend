package com.icevibe.app;

import java.math.BigDecimal;

/**
 * Clase DetalleVenta
 * COMPOSICIÓN: Parte integral de Venta (no puede existir sin Venta)
 * El ciclo de vida de DetalleVenta depende completamente de Venta
 */
public class DetalleVenta {
    private Long id;
    private Long ventaId;
    private Producto producto;
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    public DetalleVenta() {
        this.cantidad = 1;
        this.precioUnitario = BigDecimal.ZERO;
        this.subtotal = BigDecimal.ZERO;
    }
    
    public DetalleVenta(Producto producto, int cantidad, BigDecimal precioUnitario) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public void calcularSubtotal() {
        this.subtotal = precioUnitario.multiply(new BigDecimal(cantidad));
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getVentaId() {
        return ventaId;
    }
    
    public void setVentaId(Long ventaId) {
        this.ventaId = ventaId;
    }
    
    public Producto getProducto() {
        return producto;
    }
    
    public void setProducto(Producto producto) {
        this.producto = producto;
    }
    
    public int getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
        calcularSubtotal();
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubtotal();
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
