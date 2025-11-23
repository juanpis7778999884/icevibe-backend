package com.icevibe.app;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase para gestionar la conexión a la base de datos PostgreSQL,
 * compatible con entorno local y Render (producción).
 */
@Component
public class ConexionBD {

    // Si Render pone la variable DATABASE_URL → la usamos
    // Si NO existe → usa la BD local
    private static final String URL =
        (System.getenv("DATABASE_URL") != null && !System.getenv("DATABASE_URL").isEmpty())
        ? System.getenv("DATABASE_URL") + "?sslmode=require"
        : "jdbc:postgresql://localhost:5432/proyectoempresa";

    private static final String USUARIO =
        (System.getenv("DB_USER") != null && !System.getenv("DB_USER").isEmpty())
        ? System.getenv("DB_USER")
        : "postgres";

    private static final String PASSWORD =
        (System.getenv("DB_PASSWORD") != null && !System.getenv("DB_PASSWORD").isEmpty())
        ? System.getenv("DB_PASSWORD")
        : "messi777";

    public static Connection obtenerConexion() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
            return DriverManager.getConnection(URL, USUARIO, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver de PostgreSQL no encontrado", e);
        }
    }

    public static boolean verificarConexion() {
        try (Connection conn = obtenerConexion()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Error al verificar conexión: " + e.getMessage());
            return false;
        }
    }
}
