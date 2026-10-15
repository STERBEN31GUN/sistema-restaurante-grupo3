package com.sv.grupo3.restaurante.main;

import com.sv.grupo3.restaurante.repository.*;
import com.sv.grupo3.restaurante.service.*;

/**
 * Punto de entrada del prototipo del Sistema de Gestión de Restaurante.
 * Orquesta la inicialización de repositorios (.dat) y la concurrencia (Hilos).
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=== INICIANDO SISTEMA DE GESTION DE RESTAURANTE ===\n");

        // ---------------------------------------------------------
        // 1. INICIALIZAR REPOSITORIOS (Persistencia en .dat de Jason)
        // ---------------------------------------------------------
        ProductoRepository productoRepository = new ProductoRepository();
        MesaRepository mesaRepository = new MesaRepository();
        PedidoRepository pedidoRepository = new PedidoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();

        // ---------------------------------------------------------
        // 2. INICIALIZAR SERVICIOS (Lógica de Negocio y Reglas)
        // ---------------------------------------------------------
        ProductoService productoService = new ProductoService(productoRepository);
        PedidoService pedidoService = new PedidoService(pedidoRepository);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);

        // ---------------------------------------------------------
        // 3. INICIALIZAR CONCURRENCIA (Tu hilo de cocina)
        // ---------------------------------------------------------
        CocinaMonitorThread monitorCocina = new CocinaMonitorThread();
        Thread hiloCocina = new Thread(monitorCocina);
        hiloCocina.start(); // El chef empieza a esperar órdenes en segundo plano

        // ---------------------------------------------------------
        // 4. LANZAR INTERFAZ DE CONSOLA (Tarea de Alisson y Rafael)
        // ---------------------------------------------------------
        // En lugar de tener el Scanner desordenado aquí, 
        // delegamos todo a la clase MenuConsola que le pasaste a Alisson.
        MenuConsola menu = new MenuConsola(productoService, monitorCocina);
        
        // Esta línea detiene el flujo principal en el menú interactivo,
        // mientras tu hilo de cocina sigue corriendo por debajo.
        menu.mostrarMenu();

        // ---------------------------------------------------------
        // 5. APAGADO SEGURO
        // ---------------------------------------------------------
        System.out.println("\n[Sistema] Cerrando operaciones del restaurante...");
        System.exit(0);
    }
}
