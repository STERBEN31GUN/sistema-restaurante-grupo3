package com.sv.grupo3.restaurante.dao;

import com.sv.grupo3.restaurante.model.Producto;

import java.util.List;

public interface ProductoDAO extends GenericDAO<Producto> {

    List<Producto> buscarPorCategoria(String categoria);
}
