package com.sv.grupo3.restaurante.dao;

import java.util.List;

/**
 * Contrato basico de operaciones CRUD que debe cumplir cada DAO del sistema.
 *
 * @param <T> tipo de entidad (Producto, Mesa, Pedido, Usuario, etc.)
 */
public interface GenericDAO<T> {

    T guardar(T entidad);

    T buscarPorId(int id);

    List<T> listarTodos();

    T actualizar(T entidad);

    void eliminar(int id);
}
