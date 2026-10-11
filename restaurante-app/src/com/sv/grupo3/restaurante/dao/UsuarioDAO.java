package com.sv.grupo3.restaurante.dao;

import com.sv.grupo3.restaurante.model.Usuario;

public interface UsuarioDAO extends GenericDAO<Usuario> {

    Usuario buscarPorNombreUsuario(String usuario);
}
