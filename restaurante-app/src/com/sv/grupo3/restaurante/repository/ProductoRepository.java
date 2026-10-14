package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.util.GeneradorId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementacion en memoria (HashMap) del DAO de Producto.
 * Sirve como prototipo funcional; en una siguiente iteracion se
 * reemplazaria por una implementacion con JDBC / una base de datos real.
 */
public class ProductoRepository implements ProductoDAO {

    private final Map<Integer, ItemMenu> almacen = new LinkedHashMap<>();
    private final GeneradorId generadorId = new GeneradorId();

    @Override
    public ItemMenu guardar(ItemMenu entidad) {
        entidad.setId(generadorId.siguiente());
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public ItemMenu buscarPorId(int id) {
        ItemMenu item = almacen.get(id);
        if (item == null) {
            throw new RecursoNoEncontradoException("Item con id " + id + " no encontrado");
        }
        return item;
    }

    @Override
    public List<ItemMenu> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public ItemMenu actualizar(ItemMenu entidad) {
        buscarPorId(entidad.getId()); // valida que exista
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public void eliminar(int id) {
        buscarPorId(id); // valida que exista
        almacen.remove(id);
    }

    @Override
    public List<ItemMenu> buscarPorCategoria(String categoria) {
        return listarTodos().stream()
            .filter(p -> p.getClass().getSimpleName().equalsIgnoreCase(categoria))
            .collect(Collectors.toList());
    }
}
