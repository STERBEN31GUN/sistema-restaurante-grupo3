package com.sv.grupo3.restaurante.controller;

import com.sv.grupo3.restaurante.model.Bebida;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Platillo;
import com.sv.grupo3.restaurante.service.ProductoService;

import java.util.List;

/**
 * Controlador del catalogo. Recibe los datos ya capturados por la vista de consola,
 * construye la entidad correspondiente (Platillo o Bebida) y delega en ProductoService.
 *
 * La vista no conoce el servicio ni el repositorio; solo este controlador.
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 */
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    public ItemMenu registrarPlatillo(String nombre, String descripcion, double precio,
                                      int existencias, boolean requiereCoccion) {
        validarEntrada(nombre, precio, existencias);
        Platillo platillo = new Platillo(0, nombre.trim(), descripcion, precio, existencias,
                ProductoService.ESTADO_ACTIVO, requiereCoccion);
        return productoService.registrarProducto(platillo);
    }

    public ItemMenu registrarBebida(String nombre, String descripcion, double precio,
                                    int existencias, boolean contieneAlcohol) {
        validarEntrada(nombre, precio, existencias);
        Bebida bebida = new Bebida(0, nombre.trim(), descripcion, precio, existencias,
                ProductoService.ESTADO_ACTIVO, contieneAlcohol);
        return productoService.registrarProducto(bebida);
    }

    public ItemMenu consultarProducto(int idProducto) {
        validarId(idProducto, "producto");
        return productoService.buscarPorId(idProducto);
    }

    public List<ItemMenu> consultarPorCategoria(String categoria) {
        return productoService.listarPorCategoria(categoria);
    }

    public List<ItemMenu> listarCatalogo() {
        return productoService.listarTodos();
    }

    /** Menu que se muestra al cliente: unicamente productos activos. */
    public List<ItemMenu> listarMenuDisponible() {
        return productoService.listarDisponibles();
    }

    private void validarEntrada(String nombre, double precio, int existencias) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es obligatorio.");
        }
        if (precio <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
        if (existencias < 0) {
            throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        }
    }

    private void validarId(int id, String entidad) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de " + entidad + " debe ser un entero positivo.");
        }
    }
}
