package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.dao.UsuarioDAO;
import com.sv.grupo3.restaurante.model.Usuario;

import java.util.List;

/**
 * Logica de negocio para el registro y autenticacion basica de usuarios
 * (Administrador, Mesero, Cajero, Cocinero, Bartender).
 */
public class UsuarioService {

    private final UsuarioDAO usuarioDAO;

    public UsuarioService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Usuario registrarUsuario(String nombre, String rol, String usuario, String contrasena) {
        Usuario nuevo = new Usuario(0, nombre, rol, usuario, contrasena);
        return usuarioDAO.guardar(nuevo);
    }

    public boolean iniciarSesion(String usuario, String contrasena) {
        Usuario encontrado = usuarioDAO.buscarPorNombreUsuario(usuario);
        return encontrado.getContrasena().equals(contrasena);
    }

    public List<Usuario> listarTodos() {
        return usuarioDAO.listarTodos();
    }
}
