package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.GenericDAO;
import com.sv.grupo3.restaurante.model.ItemMenu;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProductoRepository implements GenericDAO<ItemMenu> {

    // Archivo binario donde se guardará todo el menú
    private static final String FILE_NAME = "productos.dat";

    @Override
    public void guardar(ItemMenu producto) {
        List<ItemMenu> productos = listarTodos();
        productos.add(producto);
        guardarTodo(productos);
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ItemMenu> listarTodos() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return new ArrayList<>(); // Si el archivo no existe, devuelve una lista vacía
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            return (List<ItemMenu>) ois.readObject(); // Deserialización
        } catch (EOFException e) {
            return new ArrayList<>(); // Archivo vacío
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer el archivo .dat: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Método privado para sobreescribir el archivo completo con la lista actualizada
    private void guardarTodo(List<ItemMenu> productos) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(productos); // Serialización
        } catch (IOException e) {
            System.err.println("Error al escribir en el archivo .dat: " + e.getMessage());
        }
    }

    @Override
    public ItemMenu buscarPorId(int id) {
        return listarTodos().stream()
                .filter(p -> p.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void actualizar(ItemMenu productoActualizado) {
        List<ItemMenu> productos = listarTodos();
        for (int i = 0; i < productos.size(); i++) {
            if (productos.get(i).getId() == productoActualizado.getId()) {
                productos.set(i, productoActualizado);
                break;
            }
        }
        guardarTodo(productos);
    }

    @Override
    public void eliminar(int id) {
        List<ItemMenu> productos = listarTodos();
        productos.removeIf(p -> p.getId() == id);
        guardarTodo(productos);
    }

    // Método extra para filtrar por categoría
    public List<ItemMenu> buscarPorCategoria(String categoria) {
        return listarTodos().stream()
                .filter(p -> p.getClass().getSimpleName().equalsIgnoreCase(categoria))
                .collect(Collectors.toList());
    }
}