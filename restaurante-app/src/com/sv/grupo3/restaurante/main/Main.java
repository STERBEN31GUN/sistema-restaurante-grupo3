package com.sv.grupo3.restaurante.main;

import com.sv.grupo3.restaurante.repository.MesaRepository;
import com.sv.grupo3.restaurante.repository.PedidoRepository;
import com.sv.grupo3.restaurante.repository.ProductoRepository;
import com.sv.grupo3.restaurante.repository.UsuarioRepository;
import com.sv.grupo3.restaurante.service.CocinaMonitorThread;
import com.sv.grupo3.restaurante.service.PedidoService;
import com.sv.grupo3.restaurante.service.ProductoService;
import com.sv.grupo3.restaurante.service.UsuarioService;

/**
 * Punto de entrada principal del Sistema de Gestión de Restaurante.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=== SISTEMA DE GESTION DE RESTAURANTE ===\n");

        // 1. Inicialización de Repositorios (Persistencia Binaria)
        ProductoRepository productoRepository = new ProductoRepository();
        MesaRepository mesaRepository = new MesaRepository();
        PedidoRepository pedidoRepository = new PedidoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();

        // 2. Inicialización de Servicios (Capa de Negocio)
        ProductoService productoService = new ProductoService(productoRepository);
        PedidoService pedidoService = new PedidoService(pedidoRepository);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);

        // 3. Inicialización de Programación Concurrente (Monitor de Cocina)
        CocinaMonitorThread monitorCocina = new CocinaMonitorThread();
        Thread hiloCocina = new Thread(monitorCocina);
        hiloCocina.start();

        // 4. Ejecución de Interfaz de Consola Interactiva
        MenuConsola menu = new MenuConsola(productoService, monitorCocina);
        menu.mostrarMenu();

        // 5. Cierre Seguro del Sistema
        System.out.println("\n[Sistema] Finalizando ejecución y servicios activos...");
        System.exit(0);
    }
}