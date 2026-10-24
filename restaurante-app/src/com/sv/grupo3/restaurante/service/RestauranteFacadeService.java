package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Mesa;
import com.sv.grupo3.restaurante.model.Pedido;

import java.util.List;

/**
 * Servicio integrador (patron Facade) que coordina los modulos operativos
 * (Cliente, Mesero, Caja) con los servicios transaccionales y el hilo de cocina.
 *
 * Los controladores hablan con esta fachada; la fachada decide a que servicio
 * delegar y en que orden. Las transacciones (commit / rollback) viven en
 * PedidoService y MesaService; aqui solo se orquesta el flujo.
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 * Cobertura: Integracion transversal de HU-001, HU-002, HU-003 y HU-004
 */
public class RestauranteFacadeService {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final MesaService mesaService;
    private final CocinaMonitorThread cocinaMonitor;

    public RestauranteFacadeService(ProductoService productoService,
                                    PedidoService pedidoService,
                                    MesaService mesaService,
                                    CocinaMonitorThread cocinaMonitor) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.mesaService = mesaService;
        this.cocinaMonitor = cocinaMonitor;
    }

    // =========================================================================
    // 1. MODULO CLIENTE: consulta del menu digital y de mesas disponibles
    // =========================================================================

    public List<ItemMenu> consultarMenuDisponibleCliente() {
        return productoService.listarDisponibles();
    }

    public List<ItemMenu> consultarMenuPorCategoria(String categoria) {
        return productoService.listarPorCategoria(categoria);
    }

    public List<Mesa> consultarMesasDisponibles() {
        return mesaService.listarDisponibles();
    }

    // =========================================================================
    // 2. MODULO MESERO: apertura de cuenta, toma de comanda y envio a cocina
    // =========================================================================

    /** Abre la cuenta de la mesa (la ocupa y crea el pedido en estado PENDIENTE). */
    public Pedido abrirCuentaMesa(int idMesa, int idUsuario) {
        Pedido pedido = pedidoService.abrirPedido(idMesa, idUsuario);
        System.out.println("[Integrador Mesero] Cuenta abierta: pedido #" + pedido.getId()
                + " para la mesa " + idMesa + ".");
        return pedido;
    }

    /**
     * Registra una comanda completa (varias lineas) en una sola transaccion y, si
     * la base de datos la confirmo, envia cada linea al hilo de cocina/bar.
     */
    public Pedido registrarComandaCompleta(int idMesa, int idUsuario, List<DetallePedido> detalles) {
        Pedido pedido = pedidoService.registrarPedido(idMesa, idUsuario, detalles);
        for (DetallePedido detalle : pedido.getDetalles()) {
            despacharACocina(detalle);
        }
        return pedido;
    }

    /**
     * Agrega un producto a un pedido abierto. Solo si el commit fue exitoso la
     * comanda se encola en el hilo de cocina; si hubo rollback nunca llega a cocina.
     */
    public DetallePedido procesarComandaMesero(int idPedido, int idProducto, int cantidad, String observaciones) {
        DetallePedido detalle = new DetallePedido();
        detalle.setIdPedido(idPedido);
        detalle.setIdProducto(idProducto);
        detalle.setCantidad(cantidad);
        detalle.setObservaciones(observaciones);

        DetallePedido registrado = pedidoService.agregarDetalle(idPedido, detalle);
        despacharACocina(registrado);

        ItemMenu item = productoService.buscarPorId(idProducto);
        System.out.println("[Integrador Mesero -> Cocina] Comanda enviada a preparacion: "
                + cantidad + "x " + item.getNombre());
        return registrado;
    }

    // =========================================================================
    // 3. MODULO CAJA: liquidacion de la cuenta
    // =========================================================================

    /**
     * Cobra la cuenta: el servicio marca el pedido como PAGADO, calcula el total
     * en base de datos y libera la mesa dentro de la misma transaccion.
     */
    public Pedido procesarCobroCaja(int idPedido) {
        Pedido pagado = pedidoService.cobrarPedido(idPedido);
        System.out.printf("[Modulo Caja] Pedido #%d liquidado exitosamente. Mesa %d liberada. Total cobrado: $%.2f%n",
                pagado.getId(), pagado.getIdMesa(), pagado.getTotal());
        return pagado;
    }

    // =========================================================================
    // Acceso a los servicios subyacentes
    // =========================================================================

    public ProductoService getProductoService() {
        return productoService;
    }

    public PedidoService getPedidoService() {
        return pedidoService;
    }

    public MesaService getMesaService() {
        return mesaService;
    }

    private void despacharACocina(DetallePedido detalle) {
        if (cocinaMonitor != null) {
            cocinaMonitor.agregarComanda(detalle);
        }
    }
}
