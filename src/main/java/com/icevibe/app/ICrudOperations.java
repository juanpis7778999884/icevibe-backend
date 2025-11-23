package com.icevibe.app;

import java.util.List;

/**
 * Interface para operaciones CRUD genéricas
 * Cumple con requisito de clase tipo interface
 * @param <T> Tipo de entidad
 */
public interface ICrudOperations<T> {
    T crear(T entidad) throws Exception;
    T obtenerPorId(Long id) throws Exception;
    List<T> obtenerTodos() throws Exception;
    T actualizar(T entidad) throws Exception;
    boolean eliminar(Long id) throws Exception;
}
