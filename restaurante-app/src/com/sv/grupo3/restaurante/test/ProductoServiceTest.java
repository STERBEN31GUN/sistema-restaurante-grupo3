package com.sv.grupo3.restaurante.test;

import com.sv.grupo3.restaurante.model.Bebida;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Platillo;
import com.sv.grupo3.restaurante.repository.ProductoRepository;
import com.sv.grupo3.restaurante.service.ProductoService;

import java.util.List;

public class ProductoServiceTest {

    public static void main(String[] args) {
        ProductoRepository repo = new ProductoRepository();
        ProductoService service = new ProductoService(repo);

        try {
            // 1. Probar registro de Platillo (Se crea el objeto y se pasa al servicio)
            Platillo p = new Platillo(1, "Yuca frita", "Con chicharron", 2.50, 15, "ACTIVO", true);
            service.registrarProducto(p);
            assert p.getId() > 0 : "El producto deberia tener un id asignado";

            // 2. Probar registro de Bebida (Se crea el objeto y se pasa al servicio)
            Bebida b = new Bebida(2, "Gaseosa", "Lata 355ml", 1.00, 20, "ACTIVO", false);
            service.registrarProducto(b);

            // 3. Probar listar por categoría ("Bebida" o "Platillo" según la clase)
            List<ItemMenu> bebidas = service.listarPorCategoria("Bebida");
            assert bebidas.size() >= 1 : "Deberia existir 1 producto";

            // 4. Probar dar de baja
            service.darDeBaja(p.getId());
            assert repo.buscarPorId(p.getId()).getEstado().equals("INACTIVO");

            System.out.println("Todas las pruebas de ProductoService pasaron exitosamente.");

        } catch (Exception e) {
            System.err.println("La prueba falló: " + e.getMessage());
        }
    }
}
