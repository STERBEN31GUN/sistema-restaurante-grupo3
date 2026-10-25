package com.sv.grupo3.restaurante.main;

import com.sv.grupo3.restaurante.config.DataSourceConfig;
import com.sv.grupo3.restaurante.controller.MesaController;
import com.sv.grupo3.restaurante.controller.PedidoController;
import com.sv.grupo3.restaurante.controller.ProductoController;
import com.sv.grupo3.restaurante.repository.MesaRepositoryJDBC;
import com.sv.grupo3.restaurante.repository.PedidoRepositoryJDBC;
import com.sv.grupo3.restaurante.repository.ProductoRepository;
import com.sv.grupo3.restaurante.service.CocinaMonitorThread;
import com.sv.grupo3.restaurante.service.MesaService;
import com.sv.grupo3.restaurante.service.PedidoService;
import com.sv.grupo3.restaurante.service.ProductoService;
import com.sv.grupo3.restaurante.service.RestauranteFacadeService;

import javax.sql.DataSource;

/**
 * Punto de entrada del Sistema de Gestion de Restaurante.
 * Arma la aplicacion de abajo hacia arriba: DataSource -> repositorios -> servicios
 * -> fachada -> controladores -> vista de consola.
 */
public class Main {

    public static void main(String[] args) {

        System.out.println("=== INICIANDO SISTEMA DE GESTION DE RESTAURANTE ===\n");

        // ---------------------------------------------------------
        // 1. POOL DE CONEXIONES (config/DataSourceConfig)
        // ---------------------------------------------------------
<<<<<<< Updated upstream
        DataSource dataSource = DataSourceConfig.getDataSource();
=======
        ProductoRepository productoRepository = new ProductoRepository(null);
        MesaRepository mesaRepository = new MesaRepository();
        PedidoRepository PedidoRepository = new PedidoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();
>>>>>>> Stashed changes

        // ---------------------------------------------------------
        // 2. REPOSITORIOS (persistencia JDBC sobre el mismo pool)
        // ---------------------------------------------------------
        ProductoRepository productoRepository = new ProductoRepository(dataSource);
        MesaRepositoryJDBC mesaRepository = new MesaRepositoryJDBC(dataSource);
        PedidoRepositoryJDBC pedidoRepository = new PedidoRepositoryJDBC(dataSource);

        // ---------------------------------------------------------
        // 3. SERVICIOS (reglas de negocio y transacciones)
        // ---------------------------------------------------------
        ProductoService productoService = new ProductoService(productoRepository);
        MesaService mesaService = new MesaService(dataSource, mesaRepository);
        PedidoService pedidoService = new PedidoService(dataSource, pedidoRepository, productoService, mesaService);

        // ---------------------------------------------------------
        // 4. CONCURRENCIA (hilo de cocina en segundo plano)
        // ---------------------------------------------------------
        CocinaMonitorThread monitorCocina = new CocinaMonitorThread();
        Thread hiloCocina = new Thread(monitorCocina, "cocina-monitor");
        hiloCocina.setDaemon(true);
        hiloCocina.start();

        // ---------------------------------------------------------
        // 5. FACHADA Y CONTROLADORES (puerta de entrada para las vistas)
        // ---------------------------------------------------------
        RestauranteFacadeService facade =
                new RestauranteFacadeService(productoService, pedidoService, mesaService, monitorCocina);
        ProductoController productoController = new ProductoController(productoService);
        MesaController mesaController = new MesaController(mesaService);
        PedidoController pedidoController = new PedidoController(facade);

        // ---------------------------------------------------------
        // 6. INTERFAZ DE CONSOLA
        //    Las vistas de mesero/cliente y cocina/bar/caja consumen los
        //    controladores anteriores (productoController, mesaController, pedidoController).
        // ---------------------------------------------------------
        MenuConsola menu = new MenuConsola(productoService, monitorCocina);
        menu.mostrarMenu();

        // ---------------------------------------------------------
        // 7. APAGADO SEGURO
        // ---------------------------------------------------------
        System.out.println("\n[Sistema] Cerrando operaciones del restaurante...");
        monitorCocina.detener();
        System.exit(0);
    }
}
