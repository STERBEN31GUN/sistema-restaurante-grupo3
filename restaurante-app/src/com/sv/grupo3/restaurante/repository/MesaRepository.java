package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.GenericDAO;
import com.sv.grupo3.restaurante.model.Mesa;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MesaRepository implements GenericDAO<Mesa> {

    private static final String FILE_NAME = "mesas.dat";

    @Override
    public Mesa guardar(Mesa mesa) {
        List<Mesa> mesas = listarTodos();
        // Si el id es 0, simulamos un autoincremento (opcional)
        if (mesa.getId() == 0) {
            int newId = mesas.isEmpty() ? 1 : mesas.get(mesas.size() - 1).getId() + 1;
            mesa.setId(newId);
        }
        mesas.add(mesa);
        guardarTodo(mesas);
        return mesa;
    }

    @Override
    public Mesa buscarPorId(int id) {
        return listarTodos().stream()
                .filter(m -> m.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Mesa> listarTodos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<Mesa>) ois.readObject();
        } catch (EOFException e) {
            return new ArrayList<>();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Error] No se pudo leer mesas.dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public Mesa actualizar(Mesa mesaActualizada) {
        List<Mesa> mesas = listarTodos();
        for (int i = 0; i < mesas.size(); i++) {
            if (mesas.get(i).getId() == mesaActualizada.getId()) {
                mesas.set(i, mesaActualizada);
                break;
            }
        }
        guardarTodo(mesas);
        return mesaActualizada;
    }

    @Override
    public void eliminar(int id) {
        List<Mesa> mesas = listarTodos();
        mesas.removeIf(m -> m.getId() == id);
        guardarTodo(mesas);
    }

    private void guardarTodo(List<Mesa> mesas) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(mesas);
        } catch (IOException e) {
            System.err.println("[Error] Fallo al escribir en mesas.dat: " + e.getMessage());
        }
    }
}
