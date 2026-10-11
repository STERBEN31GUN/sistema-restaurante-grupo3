package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.dao.PedidoDAO;
import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.Pedido;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Logica de negocio relacionada a:
 * HU-002: Registrar un pedido asociado a una mesa.
 * HU-003: Gestion de pedidos de cocina (platillos).
 * HU-004: Gestion de pedidos de bebidas.
 */
public class PedidoService {

    private final PedidoDAO pedidoDAO;

    public PedidoService(PedidoDAO pedidoDAO) {
        this.pedidoDAO = pedidoDAO;
    }

    public Pedido registrarPedido(int numeroMesa, String zona) {
        Pedido pedido = new Pedido(0, numeroMesa, zona, LocalDate.now(), LocalTime.now(), "PENDIENTE");
        return pedidoDAO.guardar(pedido);
    }

    public void agregarDetalle(int idPedido, DetallePedido detalle) {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        detalle.setIdPedido(idPedido);
        detalle.setEstadoPreparacion("PENDIENTE");
        pedido.agregarDetalle(detalle);
        pedidoDAO.actualizar(pedido);
    }

    public List<Pedido> listarPedidosPendientes() {
        return pedidoDAO.buscarPorEstado("PENDIENTE");
    }

    public void cambiarEstadoDetalle(int idPedido, int idDetalle, String nuevoEstado) {
        Pedido pedido = pedidoDAO.buscarPorId(idPedido);
        for (DetallePedido d : pedido.getDetalles()) {
            if (d.getId() == idDetalle) {
                d.setEstadoPreparacion(nuevoEstado);
            }
        }
        pedidoDAO.actualizar(pedido);
    }

    public List<Pedido> listarTodos() {
        return pedidoDAO.listarTodos();
    }
}
