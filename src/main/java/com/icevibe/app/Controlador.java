package com.icevibe.app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controlador REST completo para Ice Vibe POS
 * Incluye gestión de productos, usuarios, ventas y WhatsApp
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class Controlador {
    
    @Autowired
    private RestTemplate restTemplate;
    
    // ==================== INYECCIÓN DE DEPENDENCIAS ====================
    
    @Autowired
    private ConexionBD conexionBD;
    
    // ==================== HEALTH CHECK ====================
    
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Ice Vibe POS Backend funcionando correctamente");
        response.put("timestamp", LocalDateTime.now().toString());
        response.put("database", conexionBD.verificarConexion() ? "Conectada" : "Desconectada");
        return response;
    }
    
    // ==================== AUTENTICACIÓN ====================
    
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credenciales) {
        Map<String, Object> response = new HashMap<>();
        
        String codigo = credenciales.get("codigo");
        String password = credenciales.get("password");
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "SELECT * FROM usuarios WHERE codigo = ? AND password = ? AND activo = true";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, codigo);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("id", rs.getLong("id"));
                usuario.put("codigo", rs.getString("codigo"));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("email", rs.getString("email"));
                usuario.put("rol", rs.getString("rol"));
                
                response.put("success", true);
                response.put("message", "Login exitoso");
                response.put("usuario", usuario);
            } else {
                response.put("success", false);
                response.put("message", "Credenciales inválidas");
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al conectar con la base de datos: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== PRODUCTOS ====================
    
    @GetMapping("/productos")
    public List<Map<String, Object>> obtenerProductos() {
        List<Map<String, Object>> productos = new ArrayList<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "SELECT * FROM productos ORDER BY categoria, nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("id", rs.getLong("id"));
                producto.put("codigo", rs.getString("codigo"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("categoria", rs.getString("categoria"));
                producto.put("precio", rs.getBigDecimal("precio"));
                producto.put("descripcion", rs.getString("descripcion"));
                producto.put("stock", rs.getInt("stock"));
                producto.put("stockMinimo", rs.getInt("stock_minimo"));
                producto.put("activo", rs.getBoolean("activo"));
                
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos: " + e.getMessage());
        }
        
        return productos;
    }
    
    @GetMapping("/productos/activos")
    public List<Map<String, Object>> obtenerProductosActivos() {
        List<Map<String, Object>> productos = new ArrayList<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "SELECT * FROM productos WHERE activo = true ORDER BY categoria, nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Map<String, Object> producto = new HashMap<>();
                producto.put("id", rs.getLong("id"));
                producto.put("codigo", rs.getString("codigo"));
                producto.put("nombre", rs.getString("nombre"));
                producto.put("categoria", rs.getString("categoria"));
                producto.put("precio", rs.getBigDecimal("precio"));
                producto.put("descripcion", rs.getString("descripcion"));
                producto.put("stock", rs.getInt("stock"));
                producto.put("stockMinimo", rs.getInt("stock_minimo"));
                producto.put("activo", rs.getBoolean("activo"));
                
                productos.add(producto);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener productos activos: " + e.getMessage());
        }
        
        return productos;
    }
    
    @PostMapping("/productos")
    public Map<String, Object> crearProducto(@RequestBody Map<String, Object> productoData) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "INSERT INTO productos (codigo, nombre, descripcion, categoria, precio, stock, stock_minimo, activo) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productoData.get("codigo").toString());
            stmt.setString(2, productoData.get("nombre").toString());
            stmt.setString(3, productoData.containsKey("descripcion") ? productoData.get("descripcion").toString() : null);
            stmt.setString(4, productoData.get("categoria").toString());
            stmt.setBigDecimal(5, new BigDecimal(productoData.get("precio").toString()));
            stmt.setInt(6, Integer.parseInt(productoData.get("stock").toString()));
            stmt.setInt(7, productoData.containsKey("stockMinimo") ? 
                Integer.parseInt(productoData.get("stockMinimo").toString()) : 5);
            stmt.setBoolean(8, true);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                response.put("success", true);
                response.put("message", "Producto creado exitosamente");
                response.put("id", rs.getLong("id"));
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al crear producto: " + e.getMessage());
        }
        
        return response;
    }
    
    @PutMapping("/productos/{id}")
    public Map<String, Object> actualizarProducto(@PathVariable Long id, @RequestBody Map<String, Object> productoData) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "UPDATE productos SET nombre = ?, descripcion = ?, categoria = ?, " +
                        "precio = ?, stock = ?, stock_minimo = ?, activo = ? WHERE id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, productoData.get("nombre").toString());
            stmt.setString(2, productoData.containsKey("descripcion") ? productoData.get("descripcion").toString() : null);
            stmt.setString(3, productoData.get("categoria").toString());
            stmt.setBigDecimal(4, new BigDecimal(productoData.get("precio").toString()));
            stmt.setInt(5, Integer.parseInt(productoData.get("stock").toString()));
            stmt.setInt(6, Integer.parseInt(productoData.get("stockMinimo").toString()));
            stmt.setBoolean(7, Boolean.parseBoolean(productoData.get("activo").toString()));
            stmt.setLong(8, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.put("success", true);
                response.put("message", "Producto actualizado exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al actualizar producto: " + e.getMessage());
        }
        
        return response;
    }
    
    @DeleteMapping("/productos/{id}")
    public Map<String, Object> eliminarProducto(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            // Soft delete - solo marcar como inactivo
            String sql = "UPDATE productos SET activo = false WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.put("success", true);
                response.put("message", "Producto eliminado exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "Producto no encontrado");
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar producto: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== USUARIOS ====================
    
    @GetMapping("/usuarios")
    public List<Map<String, Object>> obtenerUsuarios() {
        List<Map<String, Object>> usuarios = new ArrayList<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "SELECT id, codigo, nombre, email, rol, activo, fecha_creacion FROM usuarios ORDER BY nombre";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Map<String, Object> usuario = new HashMap<>();
                usuario.put("id", rs.getLong("id"));
                usuario.put("codigo", rs.getString("codigo"));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("email", rs.getString("email"));
                usuario.put("rol", rs.getString("rol"));
                usuario.put("activo", rs.getBoolean("activo"));
                usuario.put("fechaCreacion", rs.getTimestamp("fecha_creacion").toString());
                
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    @PostMapping("/usuarios")
    public Map<String, Object> crearUsuario(@RequestBody Map<String, Object> usuarioData) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "INSERT INTO usuarios (codigo, nombre, email, password, rol, activo) " +
                        "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, usuarioData.get("codigo").toString());
            stmt.setString(2, usuarioData.get("nombre").toString());
            stmt.setString(3, usuarioData.get("email").toString());
            stmt.setString(4, usuarioData.get("password").toString());
            stmt.setString(5, usuarioData.get("rol").toString());
            stmt.setBoolean(6, true);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                response.put("success", true);
                response.put("message", "Usuario creado exitosamente");
                response.put("id", rs.getLong("id"));
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al crear usuario: " + e.getMessage());
        }
        
        return response;
    }
    
    @PutMapping("/usuarios/{id}")
    public Map<String, Object> actualizarUsuario(@PathVariable Long id, @RequestBody Map<String, Object> usuarioData) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            // Verificar si se incluye password para actualizar
            boolean actualizarPassword = usuarioData.containsKey("password") && usuarioData.get("password") != null;
            
            String sql;
            PreparedStatement stmt;
            
            if (actualizarPassword) {
                sql = "UPDATE usuarios SET nombre = ?, email = ?, rol = ?, activo = ?, password = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, usuarioData.get("nombre").toString());
                stmt.setString(2, usuarioData.get("email").toString());
                stmt.setString(3, usuarioData.get("rol").toString());
                stmt.setBoolean(4, Boolean.parseBoolean(usuarioData.get("activo").toString()));
                stmt.setString(5, usuarioData.get("password").toString());
                stmt.setLong(6, id);
            } else {
                sql = "UPDATE usuarios SET nombre = ?, email = ?, rol = ?, activo = ? WHERE id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, usuarioData.get("nombre").toString());
                stmt.setString(2, usuarioData.get("email").toString());
                stmt.setString(3, usuarioData.get("rol").toString());
                stmt.setBoolean(4, Boolean.parseBoolean(usuarioData.get("activo").toString()));
                stmt.setLong(5, id);
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.put("success", true);
                response.put("message", "Usuario actualizado exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al actualizar usuario: " + e.getMessage());
        }
        
        return response;
    }
    
    @PutMapping("/usuarios/{id}/cambiar-contrasena")
    public Map<String, Object> cambiarContrasena(@PathVariable Long id, @RequestBody Map<String, String> data) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String nuevaContrasena = data.get("password");
            
            if (nuevaContrasena == null || nuevaContrasena.trim().isEmpty()) {
                response.put("success", false);
                response.put("message", "La contraseña no puede estar vacía");
                return response;
            }
            
            String sql = "UPDATE usuarios SET password = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, nuevaContrasena);
            stmt.setLong(2, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.put("success", true);
                response.put("message", "Contraseña actualizada exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("success", false);
            response.put("message", "Error al actualizar contraseña: " + e.getMessage());
        }
        
        return response;
    }
    
    @DeleteMapping("/usuarios/{id}")
    public Map<String, Object> eliminarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "UPDATE usuarios SET activo = false WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                response.put("success", true);
                response.put("message", "Usuario eliminado exitosamente");
            } else {
                response.put("success", false);
                response.put("message", "Usuario no encontrado");
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al eliminar usuario: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== VENTAS ====================
    
    @PostMapping("/ventas")
    public Map<String, Object> crearVenta(@RequestBody Map<String, Object> ventaData) {
        Map<String, Object> response = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = conexionBD.obtenerConexion();
            conn.setAutoCommit(false);
            
            String numeroVenta = "V-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            
            String sqlVenta = "INSERT INTO ventas (numero_venta, fecha_venta, subtotal, impuestos, descuento, total, estado, observaciones, usuario_id, numero_mesa, numero_whatsapp, nombre_cliente) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            
            PreparedStatement stmtVenta = conn.prepareStatement(sqlVenta);
            stmtVenta.setString(1, numeroVenta);
            stmtVenta.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmtVenta.setBigDecimal(3, new BigDecimal(ventaData.get("subtotal").toString()));
            stmtVenta.setBigDecimal(4, new BigDecimal(ventaData.get("impuestos").toString()));
            stmtVenta.setBigDecimal(5, ventaData.containsKey("descuento") ? 
                new BigDecimal(ventaData.get("descuento").toString()) : BigDecimal.ZERO);
            stmtVenta.setBigDecimal(6, new BigDecimal(ventaData.get("total").toString()));
            stmtVenta.setString(7, "COMPLETADA");
            stmtVenta.setString(8, ventaData.containsKey("observaciones") ? 
                ventaData.get("observaciones").toString() : null);
            stmtVenta.setLong(9, Long.parseLong(ventaData.get("usuarioId").toString()));
            stmtVenta.setString(10, ventaData.containsKey("numeroMesa") ? 
                ventaData.get("numeroMesa").toString() : null);
            stmtVenta.setString(11, ventaData.containsKey("numeroWhatsapp") ? 
                ventaData.get("numeroWhatsapp").toString() : null);
            stmtVenta.setString(12, ventaData.containsKey("nombreCliente") ? 
                ventaData.get("nombreCliente").toString() : null);
            
            ResultSet rsVenta = stmtVenta.executeQuery();
            Long ventaId = null;
            if (rsVenta.next()) {
                ventaId = rsVenta.getLong("id");
            }
            
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> detalles = (List<Map<String, Object>>) ventaData.get("detalles");
            
            String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle);
            
            for (Map<String, Object> detalle : detalles) {
                stmtDetalle.setLong(1, ventaId);
                stmtDetalle.setLong(2, Long.parseLong(detalle.get("productoId").toString()));
                stmtDetalle.setInt(3, Integer.parseInt(detalle.get("cantidad").toString()));
                stmtDetalle.setBigDecimal(4, new BigDecimal(detalle.get("precioUnitario").toString()));
                stmtDetalle.setBigDecimal(5, new BigDecimal(detalle.get("subtotal").toString()));
                stmtDetalle.addBatch();
            }
            
            stmtDetalle.executeBatch();
            conn.commit();
            
            response.put("success", true);
            response.put("message", "Venta creada exitosamente");
            response.put("ventaId", ventaId);
            response.put("numeroVenta", numeroVenta);
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.put("success", false);
            response.put("message", "Error al crear venta: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return response;
    }
    
    @GetMapping("/ventas")
    public List<Map<String, Object>> obtenerVentas() {
        List<Map<String, Object>> ventas = new ArrayList<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sql = "SELECT v.*, u.nombre as vendedor_nombre FROM ventas v " +
                        "LEFT JOIN usuarios u ON v.usuario_id = u.id " +
                        "ORDER BY v.fecha_venta DESC LIMIT 100";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Map<String, Object> venta = new HashMap<>();
                venta.put("id", rs.getLong("id"));
                venta.put("numeroVenta", rs.getString("numero_venta"));
                venta.put("fechaVenta", rs.getTimestamp("fecha_venta").toString());
                venta.put("subtotal", rs.getBigDecimal("subtotal"));
                venta.put("impuestos", rs.getBigDecimal("impuestos"));
                venta.put("descuento", rs.getBigDecimal("descuento"));
                venta.put("total", rs.getBigDecimal("total"));
                venta.put("estado", rs.getString("estado"));
                venta.put("vendedor", rs.getString("vendedor_nombre"));
                venta.put("numeroMesa", rs.getString("numero_mesa"));
                venta.put("numeroWhatsapp", rs.getString("numero_whatsapp"));
                venta.put("nombreCliente", rs.getString("nombre_cliente"));
                venta.put("usuarioId", rs.getLong("usuario_id"));
                
                ventas.add(venta);
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
        }
        
        return ventas;
    }
    
    @GetMapping("/ventas/{id}")
    public Map<String, Object> obtenerVenta(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            String sqlVenta = "SELECT * FROM ventas WHERE id = ?";
            PreparedStatement stmtVenta = conn.prepareStatement(sqlVenta);
            stmtVenta.setLong(1, id);
            ResultSet rsVenta = stmtVenta.executeQuery();
            
            if (rsVenta.next()) {
                Map<String, Object> venta = new HashMap<>();
                venta.put("id", rsVenta.getLong("id"));
                venta.put("numeroVenta", rsVenta.getString("numero_venta"));
                venta.put("fechaVenta", rsVenta.getTimestamp("fecha_venta").toString());
                venta.put("subtotal", rsVenta.getBigDecimal("subtotal"));
                venta.put("impuestos", rsVenta.getBigDecimal("impuestos"));
                venta.put("descuento", rsVenta.getBigDecimal("descuento"));
                venta.put("total", rsVenta.getBigDecimal("total"));
                venta.put("estado", rsVenta.getString("estado"));
                venta.put("observaciones", rsVenta.getString("observaciones"));
                venta.put("numeroMesa", rsVenta.getString("numero_mesa"));
                venta.put("numeroWhatsapp", rsVenta.getString("numero_whatsapp"));
                venta.put("nombreCliente", rsVenta.getString("nombre_cliente"));
                venta.put("usuarioId", rsVenta.getLong("usuario_id"));
                
                String sqlDetalles = "SELECT dv.*, p.nombre as producto_nombre " +
                                    "FROM detalle_ventas dv " +
                                    "JOIN productos p ON dv.producto_id = p.id " +
                                    "WHERE dv.venta_id = ?";
                PreparedStatement stmtDetalles = conn.prepareStatement(sqlDetalles);
                stmtDetalles.setLong(1, id);
                ResultSet rsDetalles = stmtDetalles.executeQuery();
                
                List<Map<String, Object>> detalles = new ArrayList<>();
                while (rsDetalles.next()) {
                    Map<String, Object> detalle = new HashMap<>();
                    detalle.put("id", rsDetalles.getLong("id"));
                    detalle.put("productoNombre", rsDetalles.getString("producto_nombre"));
                    detalle.put("cantidad", rsDetalles.getInt("cantidad"));
                    detalle.put("precioUnitario", rsDetalles.getBigDecimal("precio_unitario"));
                    detalle.put("subtotal", rsDetalles.getBigDecimal("subtotal"));
                    detalle.put("productoId", rsDetalles.getLong("producto_id"));
                    detalles.add(detalle);
                }
                
                response.put("venta", venta);
                response.put("detalles", detalles);
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Venta no encontrada");
            }
            
        } catch (SQLException e) {
            response.put("success", false);
            response.put("message", "Error al obtener venta: " + e.getMessage());
        }
        
        return response;
    }
    
    @PutMapping("/ventas/{id}")
    public Map<String, Object> actualizarVenta(@PathVariable Long id, @RequestBody Map<String, Object> ventaData) {
        Map<String, Object> response = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = conexionBD.obtenerConexion();
            conn.setAutoCommit(false);
            
            String sqlVenta = "UPDATE ventas SET subtotal = ?, impuestos = ?, total = ?, numero_mesa = ?, numero_whatsapp = ?, nombre_cliente = ? WHERE id = ?";
            PreparedStatement stmtVenta = conn.prepareStatement(sqlVenta);
            stmtVenta.setBigDecimal(1, new BigDecimal(ventaData.get("subtotal").toString()));
            stmtVenta.setBigDecimal(2, new BigDecimal(ventaData.get("impuestos").toString()));
            stmtVenta.setBigDecimal(3, new BigDecimal(ventaData.get("total").toString()));
            stmtVenta.setString(4, ventaData.containsKey("numeroMesa") ? 
                ventaData.get("numeroMesa").toString() : null);
            stmtVenta.setString(5, ventaData.containsKey("numeroWhatsapp") ? 
                ventaData.get("numeroWhatsapp").toString() : null);
            stmtVenta.setString(6, ventaData.containsKey("nombreCliente") ? 
                ventaData.get("nombreCliente").toString() : null);
            stmtVenta.setLong(7, id);
            stmtVenta.executeUpdate();
            
            // Eliminar detalles anteriores
            String sqlDeleteDetalles = "DELETE FROM detalle_ventas WHERE venta_id = ?";
            PreparedStatement stmtDelete = conn.prepareStatement(sqlDeleteDetalles);
            stmtDelete.setLong(1, id);
            stmtDelete.executeUpdate();
            
            // Insertar nuevos detalles
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> detalles = (List<Map<String, Object>>) ventaData.get("detalles");
            
            String sqlDetalle = "INSERT INTO detalle_ventas (venta_id, producto_id, cantidad, precio_unitario, subtotal) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle);
            
            for (Map<String, Object> detalle : detalles) {
                stmtDetalle.setLong(1, id);
                stmtDetalle.setLong(2, Long.parseLong(detalle.get("productoId").toString()));
                stmtDetalle.setInt(3, Integer.parseInt(detalle.get("cantidad").toString()));
                stmtDetalle.setBigDecimal(4, new BigDecimal(detalle.get("precioUnitario").toString()));
                stmtDetalle.setBigDecimal(5, new BigDecimal(detalle.get("subtotal").toString()));
                stmtDetalle.addBatch();
            }
            
            stmtDetalle.executeBatch();
            conn.commit();
            
            response.put("success", true);
            response.put("message", "Venta actualizada exitosamente");
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.put("success", false);
            response.put("message", "Error al actualizar venta: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return response;
    }
    
    @DeleteMapping("/ventas/{id}")
    public Map<String, Object> eliminarVenta(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = conexionBD.obtenerConexion();
            conn.setAutoCommit(false);
            
            // Eliminar detalles primero
            String sqlDeleteDetalles = "DELETE FROM detalle_ventas WHERE venta_id = ?";
            PreparedStatement stmtDetalles = conn.prepareStatement(sqlDeleteDetalles);
            stmtDetalles.setLong(1, id);
            stmtDetalles.executeUpdate();
            
            // Eliminar venta
            String sqlDeleteVenta = "DELETE FROM ventas WHERE id = ?";
            PreparedStatement stmtVenta = conn.prepareStatement(sqlDeleteVenta);
            stmtVenta.setLong(1, id);
            int rowsAffected = stmtVenta.executeUpdate();
            
            if (rowsAffected > 0) {
                conn.commit();
                response.put("success", true);
                response.put("message", "Venta eliminada exitosamente (reverso)");
            } else {
                conn.rollback();
                response.put("success", false);
                response.put("message", "Venta no encontrada");
            }
            
        } catch (Exception e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            response.put("success", false);
            response.put("message", "Error al eliminar venta: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return response;
    }
    
    // ==================== WHATSAPP ====================
    
    @PostMapping("/whatsapp/enviar")
    public Map<String, Object> enviarWhatsApp(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String telefono = data.get("telefono").toString();
            String mensaje = data.get("mensaje").toString();
            
            // Formatear número (remover caracteres especiales)
            telefono = telefono.replaceAll("[^0-9]", "");
            
            // Codificar mensaje para URL
            String mensajeCodificado = URLEncoder.encode(mensaje, StandardCharsets.UTF_8.toString());
            
            // Crear URL de WhatsApp
            String whatsappUrl = "https://wa.me/" + telefono + "?text=" + mensajeCodificado;
            
            response.put("success", true);
            response.put("message", "URL de WhatsApp generada");
            response.put("url", whatsappUrl);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Error al generar URL de WhatsApp: " + e.getMessage());
        }
        
        return response;
    }
    
    // ==================== ESTADÍSTICAS ====================
    
    @GetMapping("/estadisticas")
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection conn = conexionBD.obtenerConexion()) {
            // Total de ventas del día
            String sqlVentasHoy = "SELECT COUNT(*) as total, COALESCE(SUM(total), 0) as monto " +
                                 "FROM ventas WHERE DATE(fecha_venta) = CURRENT_DATE";
            Statement stmtHoy = conn.createStatement();
            ResultSet rsHoy = stmtHoy.executeQuery(sqlVentasHoy);
            if (rsHoy.next()) {
                stats.put("ventasHoy", rsHoy.getInt("total"));
                stats.put("montoHoy", rsHoy.getBigDecimal("monto"));
            }
            
            // Productos con stock bajo
            String sqlStockBajo = "SELECT COUNT(*) as total FROM productos " +
                                 "WHERE stock <= stock_minimo AND activo = true";
            Statement stmtStock = conn.createStatement();
            ResultSet rsStock = stmtStock.executeQuery(sqlStockBajo);
            if (rsStock.next()) {
                stats.put("productosStockBajo", rsStock.getInt("total"));
            }
            
            // Total de productos activos
            String sqlProductos = "SELECT COUNT(*) as total FROM productos WHERE activo = true";
            Statement stmtProd = conn.createStatement();
            ResultSet rsProd = stmtProd.executeQuery(sqlProductos);
            if (rsProd.next()) {
                stats.put("totalProductos", rsProd.getInt("total"));
            }
            
            // Total de usuarios activos
            String sqlUsuarios = "SELECT COUNT(*) as total FROM usuarios WHERE activo = true";
            Statement stmtUser = conn.createStatement();
            ResultSet rsUser = stmtUser.executeQuery(sqlUsuarios);
            if (rsUser.next()) {
                stats.put("totalUsuarios", rsUser.getInt("total"));
            }
            
        } catch (SQLException e) {
            System.err.println("Error al obtener estadísticas: " + e.getMessage());
        }
        
        return stats;
    }
}
