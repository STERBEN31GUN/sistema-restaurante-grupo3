package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.model.DetallePedido; // Asegúrate de que esta clase exista
import java.util.List;
import java.util.ArrayList;

/**
 * Hilo que simula el trabajo asíncrono de la cocina y el bar.
 * Procesa los detalles de los pedidos que están en estado "En preparación"
 * sin bloquear el hilo principal (la consola donde el mesero atiende).
 */
public class CocinaMonitorThread implements Runnable {

    // Lista temporal para que puedas trabajar sin depender de la base de datos de Jason
    private List<DetallePedido> colaDePreparacion;
    private volatile boolean ejecutando; // Control seguro del hilo

    public CocinaMonitorThread() {
        this.colaDePreparacion = new ArrayList<>();
        this.ejecutando = true;
    }

    // Método para que tú (o el sistema cuando se integre) agregue platos a cocinar
    public synchronized void agregarComanda(DetallePedido detalle) {
        colaDePreparacion.add(detalle);
        System.out.println("[Cocina] Nueva comanda recibida. Hay " + colaDePreparacion.size() + " en cola.");
    }

    // Método para detener el hilo cuando el restaurante cierre
    public void detener() {
        this.ejecutando = false;
    }

    @Override
    public void run() {
        System.out.println("[Cocina] El monitor de cocina está ACTIVO y esperando comandas...");

        while (ejecutando) {
            try {
                if (!colaDePreparacion.isEmpty()) {
                    // Tomamos el primer platillo de la cola
                    DetallePedido platilloActual = colaDePreparacion.remove(0);
                    
                    System.out.println("\n[Cocina] Preparando platillo/bebida (ID: " + platilloActual.getIdProducto() + ") para la Orden " + platilloActual.getIdPedido() + "...");
                    System.out.println("[Cocina] Observaciones: " + platilloActual.getObservaciones());

                    // SIMULACIÓN DE CONCURRENCIA: Pausamos este hilo por 5 segundos simulando que el chef está cocinando
                    // Mientras esto pasa, la consola principal (del mesero) sigue funcionando sin trabarse
                    Thread.sleep(5000); 

                    // Cambiamos el estado a "Listo"
                    platilloActual.setEstadoPreparacion("Listo para entregar");
                    System.out.println("[Cocina] ¡Platillo de la orden " + platilloActual.getIdPedido() + " está LISTO!\n");
                    
                } else {
                    // Si no hay comandas, el cocinero descansa 2 segundos antes de volver a revisar la cola
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                System.out.println("[Cocina] Error en la preparación: " + e.getMessage());
                Thread.currentThread().interrupt(); // Buena práctica al atrapar InterruptedException
            }
        }
        System.out.println("[Cocina] La cocina ha cerrado. Fin del turno.");
    }
}