package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.config.DataSourceConfig;
import com.sv.grupo3.restaurante.model.DetallePedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class CocinaMonitorThread extends Thread {
    
    // Bandera volatile para asegurar visibilidad segura entre hilos
    private volatile boolean running = true;
    
    // Cola segura para recibir comandas en tiempo real desde el menú/servicios
    private final BlockingQueue<DetallePedido> colaComandas = new LinkedBlockingQueue<>();

    @Override
    public void run() {
        System.out.println("[CocinaMonitor] Monitor de cocina iniciado en segundo plano.");
        
        while (running) {
            try {
                // 1. Procesar comandas que lleguen a la cola en memoria
                DetallePedido detalle = colaComandas.poll(2, TimeUnit.SECONDS);
                if (detalle != null) {
                    procesarDetalleEnBD(detalle);
                }
                
                // 2. Sondear periódicamente la base de datos para comandas pendientes
                procesarComandasPendientesBD();
                
            } catch (InterruptedException e) {
                System.out.println("[CocinaMonitor] El hilo fue interrumpido.");
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                System.err.println("[CocinaMonitor] Error en el ciclo del monitor: " + e.getMessage());
            }
        }
        
        System.out.println("[CocinaMonitor] Monitor de cocina detenido.");
    }

    /**
     * Permite agregar una nueva comanda desde el menú de consola o servicios.
     */
    public void agregarComanda(DetallePedido detalle) {
        if (detalle != null) {
            colaComandas.offer(detalle);
            System.out.println("[CocinaMonitor] Nueva comanda encolada para la cocina.");
        }
    }

    private void procesarDetalleEnBD(DetallePedido detalle) {
        String updateSQL = "UPDATE detalles_pedido SET estado = 'EN_PREPARACION' WHERE id = ?";
        try (Connection conn = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            
            // Ajusta "detalle.getId()" según el nombre del método getter en tu clase DetallePedido
            pstmt.setInt(1, detalle.getId());
            pstmt.executeUpdate();
            System.out.println("[CocinaMonitor] Detalle de pedido procesado y actualizado a EN_PREPARACION.");
            
        } catch (SQLException e) {
            System.err.println("[CocinaMonitor] Error al actualizar la comanda en la base de datos: " + e.getMessage());
        }
    }

    private void procesarComandasPendientesBD() {
        String selectSQL = "SELECT id FROM detalles_pedido WHERE estado = 'PENDIENTE'";
        String updateSQL = "UPDATE detalles_pedido SET estado = 'EN_PREPARACION' WHERE id = ?";

        try (Connection conn = DataSourceConfig.getDataSource().getConnection();
             PreparedStatement pstmtSelect = conn.prepareStatement(selectSQL);
             ResultSet rs = pstmtSelect.executeQuery()) {

            while (rs.next() && running) {
                int idDetalle = rs.getInt("id");
                
                // Simular el tiempo de preparación asíncrona en cocina
                Thread.sleep(1500);

                try (PreparedStatement pstmtUpdate = conn.prepareStatement(updateSQL)) {
                    pstmtUpdate.setInt(1, idDetalle);
                    pstmtUpdate.executeUpdate();
                }
            }
        } catch (SQLException | InterruptedException e) {
            // Manejo silencioso de interrupciones de sondeo
        }
    }

    /**
     * Detiene el hilo de forma segura (compatible con las llamadas .detener() y .detenerMonitoreo()).
     */
    public void detener() {
        this.running = false;
        this.interrupt();
    }

    public void detenerMonitoreo() {
        detener();
    }
}