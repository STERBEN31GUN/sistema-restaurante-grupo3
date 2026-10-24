package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.dao.ProductoDAO;
import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.model.ItemMenu;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del catalogo (HU-001 y HU-005).
 *
 * Trabaja unicamente contra el contrato ProductoDAO, por lo que funciona igual con el
 * repositorio en memoria y con ProductoRepositoryJDBC. Cada operacion de este servicio
 * modifica una sola fila, de modo que el autocommit del DataSource garantiza su atomicidad;
 * las operaciones que tocan varias tablas (pedido + detalles + mesa) viven en PedidoService.
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
        return productoDAO.guardar(item);
    }

    public ItemMenu actualizarProducto(ItemMenu item) {
        validarDatosBasicos(item);
        buscarPorId(item.getId()); // valida que exista antes de actualizar
        return productoDAO.actualizar(item);
    }

    public void darDeBaja(int idProducto) {
        ItemMenu item = buscarPorId(idProducto);
        item.setEstado(ESTADO_INACTIVO);
        productoDAO.actualizar(item);
    }

    /**
     * Busca un producto por ID. Unifica el comportamiento de los repositorios:
     * el de memoria lanza la excepcion y el JDBC devuelve null; aqui siempre se lanza.
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

    public List<ItemMenu> listarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe indicar una categoria (Platillo / Bebida).");
        }
        return productoDAO.buscarPorCategoria(categoria.trim());
    }

    public List<ItemMenu> listarTodos() {
        return productoDAO.listarTodos();
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
