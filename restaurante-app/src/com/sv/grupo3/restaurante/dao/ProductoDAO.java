package com.sv.grupo3.restaurante.dao;

import com.sv.grupo3.restaurante.model.ItemMenu;

import java.util.List;

public interface ProductoDAO extends GenericDAO<ItemMenu> {

    List<ItemMenu> buscarPorCategoria(String categoria);
}
