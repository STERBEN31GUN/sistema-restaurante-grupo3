package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.exception.TransaccionException;
import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Pedido;
import com.sv.grupo3.restaurante.repository.PedidoRepositoryJDBC;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Logica de negocio de pedidos (HU-002, HU-003, HU-004) con transacciones ACID.
 *
 * Cada caso de uso que modifica mas de una tabla (pedidos, detalles_pedido y mesas)
 * se ejecuta sobre UNA conexion con setAutoCommit(false): si cualquier paso falla,
 * ya sea por una regla de negocio o por un error de base de datos, se ejecuta
 * rollback y la base de datos queda exactamente como estaba.
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 */
public class PedidoService {

    public static final String ESTADO_PENDIENTE = "PENDIENTE";
    public static final String ESTADO_EN_PREPARACION = "EN_PREPARACION";
    public static final String ESTADO_LISTO = "LISTO";
    public static final String ESTADO_PAGADO = "PAGADO";

    private static final Set<String> ESTADOS_VALIDOS =
        Set.of(ESTADO_PENDIENTE, ESTADO_EN_PREPARACION, ESTADO_LISTO, ESTADO_PAGADO);

    private final DataSource dataSource;
    private final PedidoRepositoryJDBC pedidoRepositorio;
    private final ProductoService productoService;
    private final MesaService mesaService;

    public PedidoService(DataSource dataSource,
                         PedidoRepositoryJDBC pedidoRepositorio,
                         ProductoService productoService,
                         MesaService mesaService) {
        this.dataSource = dataSource;
        this.pedidoRepositorio = pedidoRepositorio;
        this.productoService = productoService;
        this.mesaService = mesaService;
    }

    // =====================================================================
    // HU-002: Registrar pedido asociado a una mesa (transaccion completa)
    // =====================================================================

    /** Abre la cuenta de una mesa sin lineas todavia; el mesero las agrega despues. */
    public Pedido abrirPedido(int idMesa, int idUsuario) {
        return registrarPedido(idMesa, idUsuario, new ArrayList<>());
    }

    /**
     * Registra un pedido completo en una sola transaccion:
     *  1. Bloquea la mesa (SELECT ... FOR UPDATE) y valida que este disponible.
     *  2. Marca la mesa como OCUPADA.
     *  3. Inserta la cabecera en pedidos con estado PENDIENTE.
     *  4. Inserta cada linea en detalles_pedido con el precio vigente del catalogo.
     *  5. commit. Ante cualquier fallo: rollback de todo lo anterior.
     */
    public Pedido registrarPedido(int idMesa, int idUsuario, List<DetallePedido> detalles) {
        if (idMesa <= 0) {
            throw new IllegalArgumentException("Debe indicar una mesa valida.");
        }
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("Debe indicar el usuario (mesero) que registra el pedido.");
        }
        if (detalles == null) {
            throw new IllegalArgumentException("La lista de detalles no puede ser nula.");
        }

        // Validaciones de catalogo fuera de la transaccion: no bloquean filas si van a fallar.
        Pedido pedido = new Pedido(0, idMesa, idUsuario, LocalDate.now(), LocalTime.now(), ESTADO_PENDIENTE, 0.0);
        for (DetallePedido detalle : detalles) {
            prepararDetalle(detalle);
            pedido.agregarDetalle(detalle);
        }
        pedido.setTotal(pedido.calcularTotal());

        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);                 // ---- inicio de la transaccion

            mesaService.ocuparMesa(con, idMesa);      // bloquea y ocupa la mesa
            pedidoRepositorio.guardar(con, pedido);   // cabecera + detalles

            con.commit();                             // ---- todo o nada
            return pedido;
        } catch (SQLException e) {
            revertir(con);
            throw new TransaccionException(
                "No se pudo registrar el pedido de la mesa " + idMesa + "; se revirtieron los cambios.", e);
        } catch (RuntimeException e) {
            revertir(con);                            // mesa ocupada, producto inexistente, etc.
            throw e;
        } finally {
            cerrar(con);
        }
    }

    /**
     * Agrega una linea a un pedido abierto y recalcula el total en la misma transaccion.
     * La cabecera se bloquea (FOR UPDATE) para que dos meseros no pisen el total entre si.
     */
    public DetallePedido agregarDetalle(int idPedido, DetallePedido detalle) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("Debe indicar un pedido valido.");
        }
        prepararDetalle(detalle);

        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);

            Pedido pedido = pedidoRepositorio.buscarPorId(con, idPedido, true);
            if (pedido == null) {
                throw new RecursoNoEncontradoException("No se encontro un pedido con el ID: " + idPedido);
            }
            if (ESTADO_PAGADO.equals(pedido.getEstado())) {
                throw new IllegalStateException("El pedido " + idPedido + " ya fue cobrado; no admite mas productos.");
            }

            detalle.setIdPedido(idPedido);
            pedidoRepositorio.guardarDetalle(con, detalle);
            double nuevoTotal = pedidoRepositorio.calcularTotal(con, idPedido);
            pedidoRepositorio.actualizarEstadoYTotal(con, idPedido, pedido.getEstado(), nuevoTotal);

            con.commit();
            return detalle;
        } catch (SQLException e) {
            revertir(con);
            throw new TransaccionException(
                "No se pudo agregar el producto al pedido " + idPedido + "; se revirtieron los cambios.", e);
        } catch (RuntimeException e) {
            revertir(con);
            throw e;
        } finally {
            cerrar(con);
        }
    }

    // =====================================================================
    // Caja: cobro de la cuenta (transaccion completa)
    // =====================================================================

    /**
     * Liquida la cuenta en una sola transaccion:
     *  1. Bloquea la cabecera del pedido (evita doble cobro concurrente).
     *  2. Calcula el total en la base de datos a partir de las lineas persistidas.
     *  3. Marca el pedido como PAGADO con ese total.
     *  4. Libera la mesa (DISPONIBLE).
     *  5. commit. Si algo falla, rollback: la cuenta sigue abierta y la mesa ocupada.
     */
    public Pedido cobrarPedido(int idPedido) {
        if (idPedido <= 0) {
            throw new IllegalArgumentException("Debe indicar un pedido valido.");
        }

        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false);

            Pedido pedido = pedidoRepositorio.buscarPorId(con, idPedido, true);
            if (pedido == null) {
                throw new RecursoNoEncontradoException("No se encontro un pedido con el ID: " + idPedido);
            }
            if (ESTADO_PAGADO.equals(pedido.getEstado())) {
                throw new IllegalStateException("El pedido " + idPedido + " ya fue cobrado anteriormente.");
            }
            if (pedido.getDetalles().isEmpty()) {
                throw new IllegalStateException("El pedido " + idPedido + " no tiene consumo registrado; no se puede cobrar.");
            }

            double total = pedidoRepositorio.calcularTotal(con, idPedido);
            pedidoRepositorio.actualizarEstadoYTotal(con, idPedido, ESTADO_PAGADO, total);
            mesaService.liberarMesa(con, pedido.getIdMesa());

            con.commit();

            pedido.setEstado(ESTADO_PAGADO);
            pedido.setTotal(total);
            return pedido;
        } catch (SQLException e) {
            revertir(con);
            throw new TransaccionException(
                "No se pudo cobrar el pedido " + idPedido + "; la cuenta sigue abierta.", e);
        } catch (RuntimeException e) {
            revertir(con);
            throw e;
        } finally {
            cerrar(con);
        }
    }

    // =====================================================================
    // HU-003 / HU-004: seguimiento de cocina y bar
    // =====================================================================

    /** Cambio de estado de una sola fila (PENDIENTE -> EN_PREPARACION -> LISTO). */
    public Pedido cambiarEstadoPedido(int idPedido, String nuevoEstado) {
        if (nuevoEstado == null || !ESTADOS_VALIDOS.contains(nuevoEstado.toUpperCase())) {
            throw new IllegalArgumentException("Estado de pedido invalido: " + nuevoEstado
                    + ". Valores permitidos: " + ESTADOS_VALIDOS);
        }
        if (ESTADO_PAGADO.equalsIgnoreCase(nuevoEstado)) {
            throw new IllegalArgumentException("Para marcar un pedido como PAGADO utilice el cobro en caja.");
        }
        Pedido pedido = buscarPorId(idPedido);
        pedido.setEstado(nuevoEstado.toUpperCase());
        return pedidoRepositorio.actualizar(pedido);
    }

    public Pedido buscarPorId(int idPedido) {
        Pedido pedido = pedidoRepositorio.buscarPorId(idPedido);
        if (pedido == null) {
            throw new RecursoNoEncontradoException("No se encontro un pedido con el ID: " + idPedido);
        }
        return pedido;
    }

    public List<Pedido> listarPedidosPendientes() {
        return pedidoRepositorio.buscarPorEstado(ESTADO_PENDIENTE);
    }

    /** Pedidos que todavia no han pasado por caja. */
    public List<Pedido> listarPedidosActivos() {
        return pedidoRepositorio.listarTodos().stream()
                .filter(p -> !ESTADO_PAGADO.equals(p.getEstado()))
                .collect(Collectors.toList());
    }

    public List<Pedido> listarTodos() {
        return pedidoRepositorio.listarTodos();
    }

    // =====================================================================
    // Utilitarios internos
    // =====================================================================

    /** Valida el producto contra el catalogo y fija el precio vigente en la linea. */
    private void prepararDetalle(DetallePedido detalle) {
        if (detalle == null) {
            throw new IllegalArgumentException("El detalle del pedido es obligatorio.");
        }
        ItemMenu item = productoService.buscarPorId(detalle.getIdProducto());
        productoService.validarDisponibleParaVenta(item, detalle.getCantidad());
        detalle.setPrecioUnitario(item.getPrecio());
        detalle.setEstadoPreparacion(ESTADO_PENDIENTE);
    }

    private void revertir(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.rollback();
        } catch (SQLException e) {
            System.err.println("[PedidoService] No se pudo ejecutar rollback: " + e.getMessage());
        }
    }

    private void cerrar(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.setAutoCommit(true); // la conexion regresa al pool en modo normal
            con.close();
        } catch (SQLException e) {
            System.err.println("[PedidoService] No se pudo cerrar la conexion: " + e.getMessage());
        }
    }
}
