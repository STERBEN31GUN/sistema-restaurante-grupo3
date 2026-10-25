package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.GenericDAO;
import com.sv.grupo3.restaurante.model.Usuario;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import com.sv.grupo3.restaurante.dao.UsuarioDAO;

public class UsuarioRepository implements UsuarioDAO {

    private static final String FILE_NAME = "usuarios.dat";

    @Override
    public Usuario guardar(Usuario usuario) {
        List<Usuario> usuarios = listarTodos();
        if (usuario.getId() == 0) {
            int newId = usuarios.isEmpty() ? 1 : usuarios.get(usuarios.size() - 1).getId() + 1;
            usuario.setId(newId);
        }
        usuarios.add(usuario);
        guardarTodo(usuarios);
        return usuario;
    }

    @Override
    public Usuario buscarPorId(int id) {
        return listarTodos().stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Usuario> listarTodos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Usuario>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Error] No se pudo leer usuarios.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Usuario actualizar(Usuario usuarioActualizado) {
        List<Usuario> usuarios = listarTodos();
        for (int i = 0; i < usuarios.size(); i++) {
            if (usuarios.get(i).getId() == usuarioActualizado.getId()) {
                usuarios.set(i, usuarioActualizado);
                break;
            }
        }
        guardarTodo(usuarios);
        return usuarioActualizado;
    }

    @Override
    public void eliminar(int id) {
        List<Usuario> usuarios = listarTodos();
        usuarios.removeIf(u -> u.getId() == id);
        guardarTodo(usuarios);
    }

    private void guardarTodo(List<Usuario> usuarios) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            System.err.println("[Error] Fallo al escribir en usuarios.dat: " + e.getMessage());
        }
    }

    @Override
    public Usuario buscarPorNombreUsuario(String usuario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'buscarPorNombreUsuario'");
    }
}
