package com.sv.grupo3.restaurante.controller;

import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.Pedido;
import com.sv.grupo3.restaurante.service.PedidoService;
import com.sv.grupo3.restaurante.service.RestauranteFacadeService;

import java.util.List;

/**
 * Controlador de pedidos (mesero, cocina/bar y caja).
 *
 * Es el unico punto de entrada que usan las vistas de consola para todo lo
 * relacionado con comandas. Valida los datos recibidos y delega en la fachada,
 * que a su vez coordina PedidoService (transacciones) y el hilo de cocina.
 * Las excepciones de negocio (IllegalArgument, IllegalState,
 * RecursoNoEncontrado) y de transaccion (TransaccionException) llegan a la vista
 * con un mensaje claro para mostrar al usuario.
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 */
public class PedidoController {

    private final RestauranteFacadeService facade;

    public PedidoController(RestauranteFacadeService facade) {
        this.facade = facade;
    }

    // ------------------------------------------------------------------
    // Mesero (HU-002)
    // ------------------------------------------------------------------

    public Pedido abrirCuenta(int idMesa, int idUsuario) {
        validarId(idMesa, "mesa");
        validarId(idUsuario, "usuario");
        return facade.abrirCuentaMesa(idMesa, idUsuario);
    }

    public DetallePedido agregarProducto(int idPedido, int idProducto, int cantidad, String observaciones) {
        validarId(idPedido, "pedido");
        validarId(idProducto, "producto");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        return facade.procesarComandaMesero(idPedido, idProducto, cantidad,
                observaciones == null ? "" : observaciones.trim());
    }

    public Pedido registrarComanda(int idMesa, int idUsuario, List<DetallePedido> detalles) {
        validarId(idMesa, "mesa");
        validarId(idUsuario, "usuario");
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La comanda debe incluir al menos un producto.");
        }
        return facade.registrarComandaCompleta(idMesa, idUsuario, detalles);
    }

    // ------------------------------------------------------------------
    // Cocina y bar (HU-003 / HU-004)
    // ------------------------------------------------------------------

    public List<Pedido> consultarPedidosPendientes() {
        return facade.getPedidoService().listarPedidosPendientes();
    }

    public List<Pedido> consultarPedidosActivos() {
        return facade.getPedidoService().listarPedidosActivos();
    }

    public Pedido consultarPedido(int idPedido) {
        validarId(idPedido, "pedido");
        return facade.getPedidoService().buscarPorId(idPedido);
    }

    public Pedido marcarEnPreparacion(int idPedido) {
        validarId(idPedido, "pedido");
        return facade.getPedidoService().cambiarEstadoPedido(idPedido, PedidoService.ESTADO_EN_PREPARACION);
    }

    public Pedido marcarListo(int idPedido) {
        validarId(idPedido, "pedido");
        return facade.getPedidoService().cambiarEstadoPedido(idPedido, PedidoService.ESTADO_LISTO);
    }

    // ------------------------------------------------------------------
    // Caja
    // ------------------------------------------------------------------

    public Pedido cobrar(int idPedido) {
        validarId(idPedido, "pedido");
        return facade.procesarCobroCaja(idPedido);
    }

    public List<Pedido> consultarHistorial() {
        return facade.getPedidoService().listarTodos();
    }

    private void validarId(int id, String entidad) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de " + entidad + " debe ser un entero positivo.");
        }
    }
}
