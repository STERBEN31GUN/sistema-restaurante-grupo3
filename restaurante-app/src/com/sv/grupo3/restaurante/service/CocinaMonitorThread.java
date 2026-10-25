package com.sv.grupo3.restaurante.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
// IMPORTANTE: Agrega aquí el import de tu clase que genera la conexión (ej. ConexionDB)

/**
 * Hilo que simula el trabajo asíncrono de la cocina y el bar.
 * Consulta directamente a la base de datos los detalles de los pedidos 
 * que están en estado "En preparación" sin bloquear el hilo principal.
 */
public class CocinaMonitorThread implements Runnable {

    private volatile boolean ejecutando; // Control seguro del hilo

    public CocinaMonitorThread() {
        this.ejecutando = true;
    }

    // Método para detener el hilo cuando el restaurante cierre
    public void detener() {
        this.ejecutando = false;
    }

    @Override
    public void run() {
        System.out.println("[Cocina] Monitor de cocina iniciado. Revisando pedidos...");

        while (ejecutando) {
            // NOTA: Cambia "TuClaseConexion.getConnection()" por el método real que usas en tu proyecto
            /* 
            try (Connection conn = TuClaseConexion.getConnection()) {
                
                // 1. Buscar platos que están "En preparación"
                String selectQuery = "SELECT id, nombre_plato, tiempo_preparacion FROM detalle_pedido WHERE estado = 'En preparación'";
                try (PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
                     ResultSet rs = selectStmt.executeQuery()) {
                     
                    while (rs.next() && ejecutando) {
                        int idDetalle = rs.getInt("id");
                        String plato = rs.getString("nombre_plato");
                        int tiempo = rs.getInt("tiempo_preparacion"); // Asumimos que está en segundos
                        
                        System.out.println("[Cocina] Preparando: " + plato + " (ID: " + idDetalle + ")");
                        
                        // 2. Simular el tiempo que tarda el cocinero
                        try {
                            Thread.sleep(tiempo * 1000L); // Multiplicado por 1000 para convertir a milisegundos
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        
                        // 3. Actualizar el estado del plato a "Listo" en la base de datos
                        String updateQuery = "UPDATE detalle_pedido SET estado = 'Listo' WHERE id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, idDetalle);
                            updateStmt.executeUpdate();
                            System.out.println("[Cocina] Plato listo: " + plato);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("[Cocina] Error al consultar la base de datos: " + e.getMessage());
            }
            */

            // El hilo descansa 5 segundos antes de volver a preguntar a la base de datos (Polling)
            try {
                Thread.sleep(5000); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        System.out.println("[Cocina] Monitor de cocina detenido.");
    }
}
