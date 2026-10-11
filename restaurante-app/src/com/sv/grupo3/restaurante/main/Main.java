package com.sv.grupo3.restaurante.main;

import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.Mesa;
import com.sv.grupo3.restaurante.model.Pedido;
import com.sv.grupo3.restaurante.model.Producto;
import com.sv.grupo3.restaurante.model.Usuario;
import com.sv.grupo3.restaurante.repository.MesaRepository;
import com.sv.grupo3.restaurante.repository.PedidoRepository;
import com.sv.grupo3.restaurante.repository.ProductoRepository;
import com.sv.grupo3.restaurante.repository.UsuarioRepository;
import com.sv.grupo3.restaurante.service.PedidoService;
import com.sv.grupo3.restaurante.service.ProductoService;
import com.sv.grupo3.restaurante.service.UsuarioService;

/**
 * Punto de entrada del prototipo. Simula el flujo basico del sistema:
 * 1. Un administrador registra productos del menu (HU-001).
 * 2. Se registra una mesa y un usuario.
 * 3. Un mesero registra un pedido asociado a esa mesa (HU-002).
 * 4. La cocina consulta y actualiza el estado de los platillos (HU-003).
 */
public class Main {

    public static void main(String[] args) {

        // --- Repositorios (capa de persistencia en memoria) ---
        ProductoRepository productoRepository = new ProductoRepository();
        MesaRepository mesaRepository = new MesaRepository();
        PedidoRepository pedidoRepository = new PedidoRepository();
        UsuarioRepository usuarioRepository = new UsuarioRepository();

        // --- Servicios (capa de logica de negocio) ---
        ProductoService productoService = new ProductoService(productoRepository);
        PedidoService pedidoService = new PedidoService(pedidoRepository);
        UsuarioService usuarioService = new UsuarioService(usuarioRepository);

        System.out.println("=== SISTEMA DE GESTION DE RESTAURANTE (prototipo) ===\n");

        // 1. Registrar usuarios
        Usuario admin = usuarioService.registrarUsuario("Lucas Amaya", "ADMINISTRADOR", "lamaya", "1234");
        Usuario mesero = usuarioService.registrarUsuario("Alisson Lovo", "MESERO", "alovo", "1234");
        System.out.println("Usuarios registrados: " + admin + " | " + mesero);

        // 2. HU-001: Administrador registra productos del menu
        Producto pupusa = productoService.registrarProducto(
                "Pupusas revueltas", "Porcion de 3 pupusas de frijol, queso y chicharron",
                "Platos fuertes", 3.50, 50);
        Producto horchata = productoService.registrarProducto(
                "Horchata", "Bebida tradicional de morro y ajonjoli", "Bebidas", 1.25, 30);
        System.out.println("\nProductos registrados:");
        System.out.println(" - " + pupusa);
        System.out.println(" - " + horchata);

        // 3. Registrar una mesa
        Mesa mesa5 = mesaRepository.guardar(new Mesa(0, 5, "Terraza", "OCUPADA"));
        System.out.println("\nMesa registrada: " + mesa5);

        // 4. HU-002: Mesero registra un pedido asociado a la mesa
        Pedido pedido = pedidoService.registrarPedido(mesa5.getNumeroMesa(), mesa5.getZona());
        pedidoService.agregarDetalle(pedido.getId(),
                new DetallePedido(1, pedido.getId(), pupusa.getId(), 2, "Sin curtido", "PENDIENTE"));
        pedidoService.agregarDetalle(pedido.getId(),
                new DetallePedido(2, pedido.getId(), horchata.getId(), 1, "Con poco hielo", "PENDIENTE"));
        System.out.println("\nPedido registrado: " + pedidoRepository.buscarPorId(pedido.getId()));

        // 5. HU-003: Cocina consulta pedidos pendientes y cambia estado
        System.out.println("\nPedidos pendientes para cocina:");
        pedidoService.listarPedidosPendientes()
                .forEach(p -> System.out.println(" - " + p));

        pedidoService.cambiarEstadoDetalle(pedido.getId(), 1, "EN_PREPARACION");
        System.out.println("\nDetalle 1 actualizado a EN_PREPARACION -> "
                + pedidoRepository.buscarPorId(pedido.getId()));

        // 6. Filtrar productos por categoria (parte de HU-001)
        System.out.println("\nProductos en categoria 'Bebidas':");
        productoService.listarPorCategoria("Bebidas")
                .forEach(p -> System.out.println(" - " + p));

        System.out.println("\n=== Fin de la demo del prototipo ===");
    }
}
