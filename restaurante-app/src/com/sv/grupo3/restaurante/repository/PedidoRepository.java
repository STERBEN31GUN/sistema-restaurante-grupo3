package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.PedidoDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.Pedido;
import com.sv.grupo3.restaurante.util.GeneradorId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoRepository implements PedidoDAO {

    private final Map<Integer, Pedido> almacen = new LinkedHashMap<>();
    private final GeneradorId generadorId = new GeneradorId();

    @Override
    public Pedido guardar(Pedido entidad) {
        entidad.setId(generadorId.siguiente());
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Pedido buscarPorId(int id) {
        Pedido pedido = almacen.get(id);
        if (pedido == null) {
            throw new RecursoNoEncontradoException("Pedido con id " + id + " no encontrado");
        }
        return pedido;
    }

    @Override
    public List<Pedido> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Pedido actualizar(Pedido entidad) {
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
    public List<Pedido> buscarPorEstado(String estado) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido p : almacen.values()) {
            if (p.getEstado().equalsIgnoreCase(estado)) {
                resultado.add(p);
            }
        }
        return resultado;
    }
}
