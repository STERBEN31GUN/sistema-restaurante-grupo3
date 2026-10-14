package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Platillo;
import com.sv.grupo3.restaurante.model.Bebida;

import java.util.List;

public class ProductoService {

    private final ProductoDAO productoDAO;

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    // Este es el método corregido. Ya no hace un "new ItemMenu",
    // sino que recibe el objeto completo (ya sea Platillo o Bebida) que se haya creado en la consola.
    public ItemMenu registrarProducto(ItemMenu item) {
        // Aquí estarían las validaciones que le tocaban a José Alfredo
        if (item.getNombre() == null || item.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        
        // Guardamos el objeto sin importar si es Platillo o Bebida (Polimorfismo en acción)
        return productoDAO.guardar(item);
    }

    public ItemMenu actualizarProducto(ItemMenu item) {
        return productoDAO.actualizar(item);
    }

    public void darDeBaja(int idProducto) {
        ItemMenu item = productoDAO.buscarPorId(idProducto);
        if (item != null) {
            item.setEstado("INACTIVO");
            productoDAO.actualizar(item);
        }
    }

    public List<ItemMenu> listarPorCategoria(String categoria) {
        return productoDAO.buscarPorCategoria(categoria);
    }

    public List<ItemMenu> listarTodos() {
        return productoDAO.listarTodos();
    }
}

