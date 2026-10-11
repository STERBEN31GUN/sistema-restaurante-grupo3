package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.model.Producto;

import java.util.List;

/**
 * Logica de negocio relacionada a HU-001:
 * Registrar/actualizar/dar de baja productos del menu y filtrarlos por categoria.
 */
public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    public Producto registrarProducto(String nombre, String descripcion, String categoria,
                                       double precio, int existencias) {
        Producto producto = new Producto(0, nombre, descripcion, categoria, precio, existencias, "ACTIVO");
        return productoDAO.guardar(producto);
    }

    public Producto actualizarProducto(Producto producto) {
        return productoDAO.actualizar(producto);
    }

    public void darDeBaja(int idProducto) {
        Producto producto = productoDAO.buscarPorId(idProducto);
        producto.setEstado("INACTIVO");
        productoDAO.actualizar(producto);
    }

    public List<Producto> listarPorCategoria(String categoria) {
        return productoDAO.buscarPorCategoria(categoria);
    }

    public List<Producto> listarTodos() {
        return productoDAO.listarTodos();
    }
}
