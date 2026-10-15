package com.sv.grupo3.restaurante.main;

import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Pedido;
import com.sv.grupo3.restaurante.service.PedidoService;
import com.sv.grupo3.restaurante.service.ProductoService;

import java.util.List;

/**
 * Vista de consola para reportes y visualización del menú digital.
 * Desarrollado por: Rafael Eduardo Ochoa Cruz
 * Rol: Desarrollador Frontend / Consola
 * Cobertura: Vistas de pedidos activos y reporte consolidado de catálogo
 */
public class ReporteConsolaView {

    private final PedidoService pedidoService;
    private final ProductoService productoService;

    public ReporteConsolaView(PedidoService pedidoService, ProductoService productoService) {
        this.pedidoService = pedidoService;
        this.productoService = productoService;
    }

    /**
     * Muestra en pantalla el resumen de pedidos que están pendientes o en cocina.
     */
    public void mostrarPedidosPendientes() {
        System.out.println("\n========================================");
        System.out.println("       MONITOR DE PEDIDOS ACTIVOS       ");
        System.out.println("========================================");
        try {
            List<Pedido> pendientes = pedidoService.listarPedidosPendientes();
            if (pendientes == null || pendientes.isEmpty()) {
                System.out.println("[Info] No hay pedidos pendientes por atender.");
            } else {
                for (Pedido p : pendientes) {
                    System.out.printf(" - Pedido #%d | Mesa: %d | Zona: %s | Estado: %s%n",
                            p.getId(), p.getNumeroMesa(), p.getZona(), p.getEstado());
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] No se pudo recuperar la lista de pedidos: " + e.getMessage());
        }
    }

    /**
     * Genera un reporte formateado del inventario y valor total del menú digital.
     */
    public void mostrarReporteInventario() {
        System.out.println("\n========================================");
        System.out.println("      REPORTE DE INVENTARIO Y PRECIOS   ");
        System.out.println("========================================");
        try {
            List<ItemMenu> catalogo = productoService.listarTodos();
            if (catalogo == null || catalogo.isEmpty()) {
                System.out.println("[Info] El catálogo no cuenta con productos registrados.");
                return;
            }

            double valorTotalInventario = 0.0;
            int unidadesTotales = 0;

            for (ItemMenu item : catalogo) {
                double subtotal = item.getPrecio() * item.getExistencias();
                valorTotalInventario += subtotal;
                unidadesTotales += item.getExistencias();

                System.out.printf(" - %-20s | Tipo: %-8s | Precio: $%6.2f | Stock: %3d | Subtotal: $%7.2f%n",
                        item.getNombre(),
                        item.getClass().getSimpleName(),
                        item.getPrecio(),
                        item.getExistencias(),
                        subtotal);
            }

            System.out.println("----------------------------------------");
            System.out.printf("Total de Unidades Disponibles: %d%n", unidadesTotales);
            System.out.printf("Valor Estimado del Inventario: $%.2f%n", valorTotalInventario);

        } catch (Exception e) {
            System.out.println("[Error] Falló la generación del reporte: " + e.getMessage());
        }
    }
}