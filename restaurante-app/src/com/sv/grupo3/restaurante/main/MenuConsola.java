package com.sv.grupo3.restaurante.main;

import com.sv.grupo3.restaurante.model.Bebida;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Platillo;
import com.sv.grupo3.restaurante.service.CocinaMonitorThread;
import com.sv.grupo3.restaurante.service.ProductoService;

import java.util.List;
import java.util.Scanner;

/**
 * Interfaz interactiva de consola orientada a roles.
 * Desarrollado por: Alisson Alexandra Lovo
 * Rol: Desarrollador Frontend / Consola
 * Cobertura: HU-005 (Navegación e interacción con el menú digital y cocina)
 */
public class MenuConsola {

    private final ProductoService productoService;
    private final CocinaMonitorThread cocinaMonitor;

    public MenuConsola(ProductoService productoService, CocinaMonitorThread cocinaMonitor) {
        this.productoService = productoService;
        this.cocinaMonitor = cocinaMonitor;
    }

    public void mostrarMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n========================================");
            System.out.println("   SISTEMA DE GESTIÓN DE RESTAURANTE    ");
            System.out.println("========================================");
            System.out.println("1. Registrar Platillo (Cocina)");
            System.out.println("2. Registrar Bebida (Bar)");
            System.out.println("3. Consultar Menú por Categoría");
            System.out.println("4. Listar Todo el Catálogo");
            System.out.println("5. Salir del Sistema");
            System.out.print("Seleccione una opción: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Limpieza de buffer

                switch (opcion) {
                    case 1:
                        registrarPlatillo(scanner);
                        break;
                    case 2:
                        registrarBebida(scanner);
                        break;
                    case 3:
                        consultarMenuPorCategoria(scanner);
                        break;
                    case 4:
                        listarCatalogoCompleto();
                        break;
                    case 5:
                        System.out.println("\n[Consola] Cerrando sesión y deteniendo monitor de cocina...");
                        cocinaMonitor.detener();
                        break;
                    default:
                        System.out.println("[Error] Opción no válida. Intente nuevamente.");
                }
            } else {
                System.out.println("[Error] Entrada inválida. Debe ingresar un número entero.");
                scanner.next(); // Limpieza de token inválido
            }
        } while (opcion != 5);
    }

    private void registrarPlatillo(Scanner scanner) {
        try {
            System.out.println("\n--- ALTA DE PLATILLO ---");
            System.out.print("Nombre del platillo: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            System.out.print("Precio unitario ($): ");
            double precio = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Existencias iniciales: ");
            int existencias = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("¿Requiere cocción en caliente? (true/false): ");
            boolean requiereCoccion = Boolean.parseBoolean(scanner.nextLine().trim());

            Platillo platillo = new Platillo(0, nombre, descripcion, precio, existencias, "ACTIVO", requiereCoccion);
            productoService.registrarProducto(platillo);

            System.out.println("[Éxito] Platillo registrado en el catálogo.");
            platillo.mostrarDetallesPreparacion(); // Evidencia de polimorfismo en consola

        } catch (NumberFormatException e) {
            System.out.println("[Error] Formato numérico erróneo para precio o existencias.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación Negocio] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Error Inesperado] No se pudo registrar el platillo: " + e.getMessage());
        }
    }

    private void registrarBebida(Scanner scanner) {
        try {
            System.out.println("\n--- ALTA DE BEBIDA ---");
            System.out.print("Nombre de la bebida: ");
            String nombre = scanner.nextLine().trim();

            System.out.print("Descripción: ");
            String descripcion = scanner.nextLine().trim();

            System.out.print("Precio unitario ($): ");
            double precio = Double.parseDouble(scanner.nextLine().trim());

            System.out.print("Existencias iniciales: ");
            int existencias = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("¿Contiene alcohol? (true/false): ");
            boolean contieneAlcohol = Boolean.parseBoolean(scanner.nextLine().trim());

            Bebida bebida = new Bebida(0, nombre, descripcion, precio, existencias, "ACTIVO", contieneAlcohol);
            productoService.registrarProducto(bebida);

            System.out.println("[Éxito] Bebida registrada en el catálogo.");
            bebida.mostrarDetallesPreparacion(); // Evidencia de polimorfismo en consola

        } catch (NumberFormatException e) {
            System.out.println("[Error] Formato numérico erróneo para precio o existencias.");
        } catch (IllegalArgumentException e) {
            System.out.println("[Validación Negocio] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[Error Inesperado] No se pudo registrar la bebida: " + e.getMessage());
        }
    }

    private void consultarMenuPorCategoria(Scanner scanner) {
        System.out.println("\n--- FILTRO DE MENÚ ---");
        System.out.print("Ingrese el tipo a consultar (Platillo / Bebida): ");
        String categoria = scanner.nextLine().trim();

        try {
            List<ItemMenu> filtrados = productoService.listarPorCategoria(categoria);
            if (filtrados.isEmpty()) {
                System.out.println("[Info] No hay productos registrados bajo la categoría: " + categoria);
            } else {
                System.out.println("\nResultados (" + filtrados.size() + "):");
                for (ItemMenu item : filtrados) {
                    System.out.printf(" - ID: %d | %s | Precio: $%.2f | Stock: %d | Estado: %s%n",
                            item.getId(), item.getNombre(), item.getPrecio(), item.getExistencias(), item.getEstado());
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] Falló la consulta por categoría: " + e.getMessage());
        }
    }

    private void listarCatalogoCompleto() {
        System.out.println("\n--- CATÁLOGO COMPLETO ---");
        try {
            List<ItemMenu> todos = productoService.listarTodos();
            if (todos.isEmpty()) {
                System.out.println("[Info] El catálogo está vacío.");
            } else {
                for (ItemMenu item : todos) {
                    System.out.printf(" - [%s] %s | $%.2f | Stock: %d%n",
                            item.getClass().getSimpleName(), item.getNombre(), item.getPrecio(), item.getExistencias());
                }
            }
        } catch (Exception e) {
            System.out.println("[Error] Falló la recuperación del catálogo: " + e.getMessage());
        }
    }
}