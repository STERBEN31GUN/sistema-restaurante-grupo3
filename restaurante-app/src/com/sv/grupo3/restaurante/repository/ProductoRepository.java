package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.Producto;
import com.sv.grupo3.restaurante.util.GeneradorId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementacion en memoria (HashMap) del DAO de Producto.
 * Sirve como prototipo funcional; en una siguiente iteracion se
 * reemplazaria por una implementacion con JDBC / una base de datos real.
 */
public class ProductoRepository implements ProductoDAO {

    private final Map<Integer, Producto> almacen = new LinkedHashMap<>();
    private final GeneradorId generadorId = new GeneradorId();

    @Override
    public Producto guardar(Producto entidad) {
        entidad.setId(generadorId.siguiente());
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Producto buscarPorId(int id) {
        Producto producto = almacen.get(id);
        if (producto == null) {
            throw new RecursoNoEncontradoException("Producto con id " + id + " no encontrado");
        }
        return producto;
    }

    @Override
    public List<Producto> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Producto actualizar(Producto entidad) {
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
    public List<Producto> buscarPorCategoria(String categoria) {
        List<Producto> resultado = new ArrayList<>();
        for (Producto p : almacen.values()) {
            if (p.getCategoria().equalsIgnoreCase(categoria)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
