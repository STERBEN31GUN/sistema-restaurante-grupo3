package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.UsuarioDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.Usuario;
import com.sv.grupo3.restaurante.util.GeneradorId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UsuarioRepository implements UsuarioDAO {

    private final Map<Integer, Usuario> almacen = new LinkedHashMap<>();
    private final GeneradorId generadorId = new GeneradorId();

    @Override
    public Usuario guardar(Usuario entidad) {
        entidad.setId(generadorId.siguiente());
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Usuario buscarPorId(int id) {
        Usuario usuario = almacen.get(id);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario con id " + id + " no encontrado");
        }
        return usuario;
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Usuario actualizar(Usuario entidad) {
        buscarPorId(entidad.getId());
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public void eliminar(int id) {
        buscarPorId(id);
        almacen.remove(id);
    }

    @Override
    public Usuario buscarPorNombreUsuario(String usuario) {
        for (Usuario u : almacen.values()) {
            if (u.getUsuario().equalsIgnoreCase(usuario)) {
                return u;
            }
        }
        throw new RecursoNoEncontradoException("Usuario '" + usuario + "' no encontrado");
    }
}
