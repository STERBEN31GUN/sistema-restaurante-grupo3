package com.sv.grupo3.restaurante.test;

import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.repository.ProductoRepository;
import com.sv.grupo3.restaurante.service.ProductoService;

import java.util.List;

/**
 * Prueba manual (sin JUnit, para no requerir dependencias externas en el prototipo).
 * Se ejecuta con su propio metodo main y valida con asserts simples.
 * Ejecutar: java com.sv.grupo3.restaurante.test.ProductoServiceTest
 */
public class ProductoServiceTest {

    public static void main(String[] args) {
        ProductoRepository repo = new ProductoRepository();
        ProductoService service = new ProductoService(repo);

        ItemMenu p = service.registrarProducto("Yuca frita", "Con curtido y salsa", "Entradas", 2.00, 20);
        assert p.getId() > 0 : "El producto deberia tener un id asignado";

        service.registrarProducto("Gaseosa", "Lata 355ml", "Bebidas", 1.00, 40);
        List<ItemMenu> bebidas = service.listarPorCategoria("Bebidas");
        assert bebidas.size() == 1 : "Deberia existir 1 producto en categoria Bebidas";

        service.darDeBaja(p.getId());
        assert repo.buscarPorId(p.getId()).getEstado().equals("INACTIVO") : "El producto deberia quedar INACTIVO";

        System.out.println("Todas las pruebas de ProductoService pasaron correctamente.");
    }
}
