package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.MesaDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.Mesa;
import com.sv.grupo3.restaurante.util.GeneradorId;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MesaRepository implements MesaDAO {

    private final Map<Integer, Mesa> almacen = new LinkedHashMap<>();
    private final GeneradorId generadorId = new GeneradorId();

    @Override
    public Mesa guardar(Mesa entidad) {
        entidad.setId(generadorId.siguiente());
        almacen.put(entidad.getId(), entidad);
        return entidad;
    }

    @Override
    public Mesa buscarPorId(int id) {
        Mesa mesa = almacen.get(id);
        if (mesa == null) {
            throw new RecursoNoEncontradoException("Mesa con id " + id + " no encontrada");
        }
        return mesa;
    }

    @Override
    public List<Mesa> listarTodos() {
        return new ArrayList<>(almacen.values());
    }

    @Override
    public Mesa actualizar(Mesa entidad) {
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
    public Mesa buscarPorNumero(int numeroMesa) {
        for (Mesa m : almacen.values()) {
            if (m.getNumeroMesa() == numeroMesa) {
                return m;
            }
        }
        throw new RecursoNoEncontradoException("Mesa numero " + numeroMesa + " no encontrada");
    }
}
