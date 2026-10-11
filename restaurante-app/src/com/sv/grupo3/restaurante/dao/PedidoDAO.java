package com.sv.grupo3.restaurante.dao;

import com.sv.grupo3.restaurante.model.Pedido;

import java.util.List;

public interface PedidoDAO extends GenericDAO<Pedido> {

    List<Pedido> buscarPorEstado(String estado);
}
