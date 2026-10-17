package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Pedido;
import com.sv.grupo3.restaurante.model.ReporteVenta;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio Integrador (Patrón Facade) que coordina la comunicación entre los
 * módulos operativos (Cliente, Mesero, Caja) y la capa de servicios/concurrencia.
 * 
 * Desarrollado por: Wilfredo Antonio Martínez Zometa
 * Rol: Desarrollador Backend / Integrador de Módulos
 * Cobertura: Integración transversal de HU-001, HU-002, HU-003 y HU-004
 */
public class RestauranteFacadeService {

    private final ProductoService productoService;
    private final PedidoService pedidoService;
    private final UsuarioService usuarioService;
    private final CocinaMonitorThread cocinaMonitor;

    public RestauranteFacadeService(ProductoService productoService,
                                    PedidoService pedidoService,
                                    UsuarioService usuarioService,
                                    CocinaMonitorThread cocinaMonitor) {
        this.productoService = productoService;
        this.pedidoService = pedidoService;
        this.usuarioService = usuarioService;
        this.cocinaMonitor = cocinaMonitor;
    }

    // =========================================================================
    // 1. MÓDULO CLIENTE: Consulta de Menú Digital Activo
    // =========================================================================

    /**
     * Recupera el catálogo disponible validando que los ítems tengan stock y estén activos.
     */
    public List<ItemMenu> consultarMenuDisponibleCliente() {
        return productoService.listarTodos().stream()
                .filter(item -> "ACTIVO".equalsIgnoreCase(item.getEstado()) && item.getExistencias() > 0)
                .toList();
    }

    // =========================================================================
    // 2. MÓDULO MESERO: Toma de Comandas e Integración con Hilo de Cocina
    // =========================================================================

    /**
     * Orquesta el flujo completo de una comanda:
     * 1. Verifica existencia del producto en el catálogo (.dat).
     * 2. Valida disponibilidad de stock.
     * 3. Descuenta existencias y actualiza la persistencia.
     * 4. Registra el detalle en el pedido de la mesa.
     * 5. Encola automáticamente el platillo/bebida en el hilo concurrente de cocina.
     */
    public void procesarComandaMesero(int idPedido, int idProducto, int cantidad, String observaciones) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Error Operativo: La cantidad solicitada debe ser mayor a cero.");
        }

        // A. Validar producto y stock en catálogo
        List<ItemMenu> catalogo = productoService.listarTodos();
        ItemMenu itemSeleccionado = catalogo.stream()
                .filter(p -> p.getId() == idProducto)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró ningún producto en el menú con el ID: " + idProducto));

        if (itemSeleccionado.getExistencias() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para '" + itemSeleccionado.getNombre()
                    + "'. Disponible: " + itemSeleccionado.getExistencias());
        }

        // B. Descontar inventario y persistir cambio
        itemSeleccionado.setExistencias(itemSeleccionado.getExistencias() - cantidad);
        if (itemSeleccionado.getExistencias() == 0) {
            itemSeleccionado.setEstado("AGOTADO");
        }
        productoService.actualizarProducto(itemSeleccionado);

        // C. Crear detalle y anexarlo al pedido activo
        int idDetalleGenerado = (int) (System.currentTimeMillis() % 100000);
        DetallePedido nuevoDetalle = new DetallePedido(
                idDetalleGenerado,
                idPedido,
                idProducto,
                cantidad,
                observaciones,
                "PENDIENTE"
        );
        pedidoService.agregarDetalle(idPedido, nuevoDetalle);

        // D. Despachar al hilo secundario de cocina (Concurrencia en tiempo real)
        cocinaMonitor.agregarComanda(nuevoDetalle);
        System.out.println("[Integrador Mesero -> Cocina] Comanda enviada a preparación: "
                + cantidad + "x " + itemSeleccionado.getNombre());
    }

    // =========================================================================
    // 3. MÓDULO CAJA: Liquidación de Cuenta y Generación de Reporte de Venta
    // =========================================================================

    /**
     * Calcula el total a pagar de un pedido cruzando los detalles con los precios
     * unitarios del catálogo, cierra la orden y emite el comprobante de venta.
     */
    public ReporteVenta procesarCobroCaja(int idPedido) {
        Pedido pedido = pedidoService.listarPedidosPendientes().stream()
                .filter(p -> p.getId() == idPedido)
                .findFirst()
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró un pedido activo o pendiente con el ID: " + idPedido));

        double totalCuenta = 0.0;
        List<ItemMenu> catalogo = productoService.listarTodos();

        for (DetallePedido detalle : pedido.getDetalles()) {
            double precioUnitario = catalogo.stream()
                    .filter(item -> item.getId() == detalle.getIdProducto())
                    .mapToDouble(ItemMenu::getPrecio)
                    .findFirst()
                    .orElse(0.0);

            totalCuenta += (precioUnitario * detalle.getCantidad());
            pedidoService.cambiarEstadoDetalle(idPedido, detalle.getId(), "ENTREGADO");
        }

        // Generar registro de liquidación para Caja compatible con ReporteVenta.java
        int idReporte = (int) (System.currentTimeMillis() % 100000);
        ReporteVenta ticketVenta = new ReporteVenta(
                idReporte,
                LocalDate.now(),
                "TURNO_ACTUAL",
                totalCuenta,
                "Consolidado Pedido #" + pedido.getId()
        );

        System.out.printf("[Módulo Caja] Pedido #%d liquidado exitosamente. Total cobrado: $%.2f%n",
                pedido.getId(), totalCuenta);

        return ticketVenta;
    }

    public ProductoService getProductoService() {
        return productoService;
    }

    public PedidoService getPedidoService() {
        return pedidoService;
    }

    public UsuarioService getUsuarioService() {
        return usuarioService;
    }
}