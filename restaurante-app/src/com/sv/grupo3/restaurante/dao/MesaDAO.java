package com.sv.grupo3.restaurante.dao;

import com.sv.grupo3.restaurante.model.Mesa;

public interface MesaDAO extends GenericDAO<Mesa> {

    Mesa buscarPorNumero(int numeroMesa);
}
