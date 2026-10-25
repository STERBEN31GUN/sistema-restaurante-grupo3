package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.ItemMenu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del catalogo (HU-001 y HU-005).
 *
 * Trabaja unicamente contra el contrato ProductoDAO (obtenerTodos, buscarPorId, guardar),
 * que implementa ProductoRepository sobre JDBC. Cada operacion modifica una sola fila, de
 * modo que el autocommit del DataSource garantiza su atomicidad; las operaciones que tocan
 * varias tablas (pedido + detalles + mesa) viven en PedidoService.
 */
public class ProductoService {

    public static final String ESTADO_ACTIVO = "ACTIVO";
    public static final String ESTADO_INACTIVO = "INACTIVO";

    private final ProductoDAO productoDAO;

    public ProductoService(ProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    /**
     * Recibe el objeto completo (Platillo o Bebida) ya construido por la capa de
     * presentacion y lo persiste sin importar la subclase (polimorfismo).
     */
    public ItemMenu registrarProducto(ItemMenu item) {
        validarDatosBasicos(item);
        if (item.getEstado() == null || item.getEstado().trim().isEmpty()) {
            item.setEstado(ESTADO_ACTIVO);
        }
        if (!productoDAO.guardar(item)) {
            throw new IllegalStateException("No se pudo guardar el producto '" + item.getNombre() + "' en el catalogo.");
        }
        return item;
    }

    /**
     * Busca un producto por ID. El repositorio devuelve null cuando no existe;
     * aqui se traduce a una excepcion de negocio para toda la capa superior.
     */
    public ItemMenu buscarPorId(int idProducto) {
        ItemMenu item = productoDAO.buscarPorId(idProducto);
        if (item == null) {
            throw new RecursoNoEncontradoException("No se encontro ningun producto en el menu con el ID: " + idProducto);
        }
        return item;
    }

    /**
     * Regla de venta usada por PedidoService antes de registrar una linea de comanda.
     * Las existencias solo se validan cuando el item las reporta (mayores a cero).
     */
    public void validarDisponibleParaVenta(ItemMenu item, int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a cero.");
        }
        if (!ESTADO_ACTIVO.equalsIgnoreCase(item.getEstado())) {
            throw new IllegalStateException("El producto '" + item.getNombre() + "' no esta activo en el menu.");
        }
        if (item.getExistencias() > 0 && item.getExistencias() < cantidad) {
            throw new IllegalStateException("Stock insuficiente para '" + item.getNombre()
                    + "'. Disponible: " + item.getExistencias());
        }
    }

    /** Filtra por tipo de item: "Platillo" o "Bebida" (nombre de la subclase). */
    public List<ItemMenu> listarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar una categoria (Platillo / Bebida).");
        }
        String tipo = categoria.trim();
        return listarTodos().stream()
                .filter(item -> item.getClass().getSimpleName().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());
    }

    public List<ItemMenu> listarTodos() {
        return productoDAO.obtenerTodos();
    }

    /** Menu que ve el cliente: solo productos activos. */
    public List<ItemMenu> listarDisponibles() {
        return listarTodos().stream()
                .filter(item -> ESTADO_ACTIVO.equalsIgnoreCase(item.getEstado()))
                .collect(Collectors.toList());
    }

    private void validarDatosBasicos(ItemMenu item) {
        if (item == null) {
            throw new IllegalArgumentException("El producto es obligatorio.");
        }
        if (item.getNombre() == null || item.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (item.getPrecio() <= 0) {
            throw new IllegalArgumentException("El precio debe ser mayor a cero.");
        }
    }
}
