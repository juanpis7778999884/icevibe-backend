package com.icevibe.app;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase Venta que extiende de EntidadBase
 * COMPOSICIÓN: Venta contiene DetalleVenta (ciclo de vida dependiente)
 * AGREGACIÓN: Venta tiene referencia a Usuario (Usuario existe independientemente)
 * HERENCIA: Venta extiende de EntidadBase
 */
public class Venta extends EntidadBase {
    private String numeroVenta;
    private LocalDateTime fechaVenta;
    private BigDecimal subtotal;
    private BigDecimal impuestos;
    private BigDecimal descuento;
    private BigDecimal total;
    private EstadoVenta estado;
    private String observaciones;
    private String numeroMesa;
    private String numeroWhatsapp;
    private String nombreCliente;
    
    private Long usuarioId;
    private Usuario usuario;
    
    private List<DetalleVenta> detalles;
    
    public Venta() {
        super();
        this.fechaVenta = LocalDateTime.now();
        this.estado = EstadoVenta.PENDIENTE;
        this.detalles = new ArrayList<>();
        this.subtotal = BigDecimal.ZERO;
        this.impuestos = BigDecimal.ZERO;
        this.descuento = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }
    
    @Override
    public String obtenerIdentificador() {
        return numeroVenta;
    }
    
    public void agregarDetalle(DetalleVenta detalle) {
        this.detalles.add(detalle);
        calcularTotales();
    }
    
    public void eliminarDetalle(DetalleVenta detalle) {
        this.detalles.remove(detalle);
        calcularTotales();
    }
    
    public void calcularTotales() {
        this.subtotal = detalles.stream()
            .map(DetalleVenta::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        this.impuestos = subtotal.multiply(new BigDecimal("0.15"));
        this.total = subtotal.add(impuestos).subtract(descuento);
        actualizarFecha();
    }
    
    public void completar() {
        this.estado = EstadoVenta.COMPLETADA;
        actualizarFecha();
    }
    
    public void cancelar() {
        this.estado = EstadoVenta.CANCELADA;
        actualizarFecha();
    }
    
    public String getNumeroVenta() {
        return numeroVenta;
    }
    
    public void setNumeroVenta(String numeroVenta) {
        this.numeroVenta = numeroVenta;
    }
    
    public LocalDateTime getFechaVenta() {
        return fechaVenta;
    }
    
    public void setFechaVenta(LocalDateTime fechaVenta) {
        this.fechaVenta = fechaVenta;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
    
    public BigDecimal getImpuestos() {
        return impuestos;
    }
    
    public void setImpuestos(BigDecimal impuestos) {
        this.impuestos = impuestos;
    }
    
    public BigDecimal getDescuento() {
        return descuento;
    }
    
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
        calcularTotales();
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public EstadoVenta getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = EstadoVenta.fromString(estado);
    }
    
    public void setEstado(EstadoVenta estado) {
        this.estado = estado;
    }
    
    public String getObservaciones() {
        return observaciones;
    }
    
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    
    public String getNumeroMesa() {
        return numeroMesa;
    }
    
    public void setNumeroMesa(String numeroMesa) {
        this.numeroMesa = numeroMesa;
    }
    
    public String getNumeroWhatsapp() {
        return numeroWhatsapp;
    }
    
    public void setNumeroWhatsapp(String numeroWhatsapp) {
        this.numeroWhatsapp = numeroWhatsapp;
    }
    
    public String getNombreCliente() {
        return nombreCliente;
    }
    
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public List<DetalleVenta> getDetalles() {
        return detalles;
    }
    
    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}
