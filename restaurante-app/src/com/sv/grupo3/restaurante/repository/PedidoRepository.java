package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.GenericDAO;
import com.sv.grupo3.restaurante.model.Pedido;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository implements GenericDAO<Pedido> {

    private static final String FILE_NAME = "pedidos.dat";

    @Override
    public Pedido guardar(Pedido pedido) {
        List<Pedido> pedidos = listarTodos();
        if (pedido.getId() == 0) {
            int newId = pedidos.isEmpty() ? 1 : pedidos.get(pedidos.size() - 1).getId() + 1;
            pedido.setId(newId);
        }
        pedidos.add(pedido);
        guardarTodo(pedidos);
        return pedido;
    }

    @Override
    public Pedido buscarPorId(int id) {
        return listarTodos().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Pedido> listarTodos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Pedido>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Error] No se pudo leer pedidos.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Pedido actualizar(Pedido pedidoActualizado) {
        List<Pedido> pedidos = listarTodos();
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).getId() == pedidoActualizado.getId()) {
                pedidos.set(i, pedidoActualizado);
                break;
            }
        }
        guardarTodo(pedidos);
        return pedidoActualizado;
    }

    @Override
    public void eliminar(int id) {
        List<Pedido> pedidos = listarTodos();
        pedidos.removeIf(p -> p.getId() == id);
        guardarTodo(pedidos);
    }

    private void guardarTodo(List<Pedido> pedidos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(pedidos);
        } catch (IOException e) {
            System.err.println("[Error] Fallo al escribir en pedidos.dat: " + e.getMessage());
        }
    }
}
