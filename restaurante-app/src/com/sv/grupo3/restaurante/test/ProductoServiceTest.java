package com.sv.grupo3.restaurante.test;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.model.Bebida;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Platillo;
import com.sv.grupo3.restaurante.service.ProductoService;

import java.util.ArrayList;
import java.util.List;

/**
 * Prueba manual de ProductoService. Usa un ProductoDAO en memoria para no depender
 * de la base de datos; la version con JUnit y Mockito vive en src/test/java.
 */
public class ProductoServiceTest {

    /** Doble de prueba del contrato ProductoDAO. */
    static class ProductoDAOEnMemoria implements ProductoDAO {
        private final List<ItemMenu> almacen = new ArrayList<>();
        private int secuencia = 0;

        @Override
        public List<ItemMenu> obtenerTodos() { return new ArrayList<>(almacen); }

        @Override
        public ItemMenu buscarPorId(int id) {
            return almacen.stream().filter(i -> i.getId() == id).findFirst().orElse(null);
        }

        @Override
        public boolean guardar(ItemMenu item) {
            item.setId(++secuencia);
            return almacen.add(item);
        }
    }

    public static void main(String[] args) {
        ProductoDAOEnMemoria repo = new ProductoDAOEnMemoria();
        ProductoService service = new ProductoService(repo);

        try {
            // 1. Registro de Platillo: el servicio asigna id a traves del DAO
            Platillo p = new Platillo(0, "Yuca frita", "Con chicharron", 2.50, 15, "ACTIVO", true);
            service.registrarProducto(p);
            assert p.getId() > 0 : "El producto deberia tener un id asignado";

            // 2. Registro de Bebida (polimorfismo: misma operacion, distinta subclase)
            Bebida b = new Bebida(0, "Gaseosa", "Lata 355ml", 1.00, 20, "ACTIVO", false);
            service.registrarProducto(b);

            // 3. Listar por categoria ("Bebida" o "Platillo" segun la clase)
            List<ItemMenu> bebidas = service.listarPorCategoria("Bebida");
            assert bebidas.size() == 1 : "Deberia existir 1 bebida";

            // 4. Busqueda por id y regla de venta
            ItemMenu encontrado = service.buscarPorId(p.getId());
            assert encontrado.getNombre().equals("Yuca frita");
            service.validarDisponibleParaVenta(encontrado, 5);

            // 5. Validaciones de negocio
            boolean rechazado = false;
            try {
                service.registrarProducto(new Platillo(0, "", 1.0));
            } catch (IllegalArgumentException e) {
                rechazado = true;
            }
            assert rechazado : "Un producto sin nombre debe rechazarse";

            System.out.println("Todas las pruebas de ProductoService pasaron exitosamente.");

        } catch (Exception e) {
            System.err.println("La prueba falló: " + e.getMessage());
        }
    }
}
