package com.icevibe.app;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos PostgreSQL
 * Soporta variables de entorno para deployment en Render
 */
@Component
public class ConexionBD {
    
    // Configuración con variables de entorno para producción
    private static final String URL = System.getenv("DATABASE_URL") != null 
        ? System.getenv("DATABASE_URL") 
        : "jdbc:postgresql://localhost:5432/proyectoempresa";
    
    private static final String USUARIO = System.getenv("DB_USER") != null 
        ? System.getenv("DB_USER") 
        : "postgres";
    
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
        ? System.getenv("DB_PASSWORD") 
        : "messi777";
    
    /**
     * Obtiene una conexión a la base de datos
     * @return 
     * @throws java.sql.SQLException
     */
    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            Connection conexion = DriverManager.getConnection(URL, USUARIO, PASSWORD);
            return conexion;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de PostgreSQL no encontrado", e);
        }
    }
    
    /**
     * Verifica si la conexión a la base de datos está funcionando
     * @return 
     */
    public static boolean verificarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            return false;
        }
    }
}
