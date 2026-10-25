package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.PedidoDAO;
import com.sv.grupo3.restaurante.model.DetallePedido;
import com.sv.grupo3.restaurante.model.Pedido;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio JDBC para la entidad Pedido y sus detalles (tablas pedidos y detalles_pedido).
 *
 * Cada metodo publico del contrato GenericDAO abre su propia conexion desde el pool.
 * Ademas se exponen sobrecargas que reciben una Connection ya abierta: estas son las que
 * usa PedidoService para que la insercion del pedido, de sus detalles y el cambio de estado
 * de la mesa ocurran dentro de UNA sola transaccion (commit / rollback controlado por el servicio).
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 */
public class PedidoRepositoryJDBC implements PedidoDAO {

    private static final String SQL_INSERT_PEDIDO =
        "INSERT INTO pedidos (mesa_id, usuario_id, estado, total) VALUES (?, ?, ?, ?)";
    private static final String SQL_INSERT_DETALLE =
        "INSERT INTO detalles_pedido (pedido_id, producto_id, cantidad, precio_unitario) VALUES (?, ?, ?, ?)";
    private static final String SQL_SELECT_PEDIDO =
        "SELECT id, mesa_id, usuario_id, fecha, estado, total FROM pedidos";
    private static final String SQL_SELECT_BY_ID = SQL_SELECT_PEDIDO + " WHERE id = ?";
    private static final String SQL_SELECT_BY_ID_FOR_UPDATE = SQL_SELECT_BY_ID + " FOR UPDATE";
    private static final String SQL_SELECT_ALL = SQL_SELECT_PEDIDO + " ORDER BY fecha, id";
    private static final String SQL_SELECT_BY_ESTADO = SQL_SELECT_PEDIDO + " WHERE estado = ? ORDER BY fecha, id";
    private static final String SQL_SELECT_DETALLES =
        "SELECT id, pedido_id, producto_id, cantidad, precio_unitario FROM detalles_pedido WHERE pedido_id = ? ORDER BY id";
    private static final String SQL_UPDATE_ESTADO_TOTAL =
        "UPDATE pedidos SET estado = ?, total = ? WHERE id = ?";
    private static final String SQL_SUM_TOTAL =
        "SELECT COALESCE(SUM(cantidad * precio_unitario), 0) FROM detalles_pedido WHERE pedido_id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM pedidos WHERE id = ?";

    private final DataSource dataSource;

    public PedidoRepositoryJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // =====================================================================
    // Contrato GenericDAO / PedidoDAO (cada metodo gestiona su conexion)
    // =====================================================================

    /**
     * Guarda el pedido y sus detalles de forma atomica. Si falla la insercion de
     * cualquier detalle se revierte tambien la cabecera.
     */
    @Override
    public Pedido guardar(Pedido pedido) {
        try (Connection con = dataSource.getConnection()) {
            con.setAutoCommit(false);
            try {
                Pedido guardado = guardar(con, pedido);
                con.commit();
                return guardado;
            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo guardar el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public Pedido buscarPorId(int id) {
        try (Connection con = dataSource.getConnection()) {
            return buscarPorId(con, id, false);
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo buscar el pedido: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> listarTodos() {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            List<Pedido> pedidos = new ArrayList<>();
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
            for (Pedido p : pedidos) {
                p.setDetalles(listarDetalles(con, p.getId()));
            }
            return pedidos;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo listar los pedidos: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Pedido> buscarPorEstado(String estado) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ESTADO)) {

            ps.setString(1, estado);
            List<Pedido> pedidos = new ArrayList<>();
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
            for (Pedido p : pedidos) {
                p.setDetalles(listarDetalles(con, p.getId()));
            }
            return pedidos;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo buscar pedidos por estado: " + e.getMessage(), e);
        }
    }

    /** Actualiza unicamente estado y total; los detalles se gestionan con guardarDetalle. */
    @Override
    public Pedido actualizar(Pedido pedido) {
        try (Connection con = dataSource.getConnection()) {
            actualizarEstadoYTotal(con, pedido.getId(), pedido.getEstado(), pedido.getTotal());
            return pedido;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo actualizar el pedido: " + e.getMessage(), e);
        }
    }

    /** Elimina la cabecera; los detalles se borran por ON DELETE CASCADE definido en schema.sql. */
    @Override
    public void eliminar(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo eliminar el pedido: " + e.getMessage(), e);
        }
    }

    // =====================================================================
    // Operaciones sobre una conexion compartida (participan en la
    // transaccion que abre la capa de servicio)
    // =====================================================================

    /** Inserta la cabecera y cada detalle usando la conexion recibida. No hace commit. */
    public Pedido guardar(Connection con, Pedido pedido) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_PEDIDO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, pedido.getIdMesa());
            ps.setInt(2, pedido.getIdUsuario());
            ps.setString(3, pedido.getEstado());
            ps.setDouble(4, pedido.getTotal());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    pedido.setId(keys.getInt(1));
                }
            }
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            detalle.setIdPedido(pedido.getId());
            guardarDetalle(con, detalle);
        }
        return pedido;
    }

    /** Inserta una linea de la comanda sobre la conexion recibida. No hace commit. */
    public DetallePedido guardarDetalle(Connection con, DetallePedido detalle) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERT_DETALLE, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, detalle.getIdPedido());
            ps.setInt(2, detalle.getIdProducto());
            ps.setInt(3, detalle.getCantidad());
            ps.setDouble(4, detalle.getPrecioUnitario());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    detalle.setId(keys.getInt(1));
                }
            }
            return detalle;
        }
    }

    /**
     * Busca un pedido con sus detalles. Con bloquear = true se usa SELECT ... FOR UPDATE,
     * de modo que dos cajeros no puedan cobrar la misma cuenta al mismo tiempo.
     */
    public Pedido buscarPorId(Connection con, int id, boolean bloquear) throws SQLException {
        String sql = bloquear ? SQL_SELECT_BY_ID_FOR_UPDATE : SQL_SELECT_BY_ID;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Pedido pedido = mapearPedido(rs);
                pedido.setDetalles(listarDetalles(con, id));
                return pedido;
            }
        }
    }

    public void actualizarEstadoYTotal(Connection con, int idPedido, String estado, double total) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ESTADO_TOTAL)) {
            ps.setString(1, estado);
            ps.setDouble(2, total);
            ps.setInt(3, idPedido);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("El pedido " + idPedido + " no existe; no se actualizo ninguna fila.");
            }
        }
    }

    /** Total calculado por la base de datos a partir de las lineas persistidas. */
    public double calcularTotal(Connection con, int idPedido) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_SUM_TOTAL)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }
        }
    }

    public List<DetallePedido> listarDetalles(Connection con, int idPedido) throws SQLException {
        List<DetallePedido> detalles = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement(SQL_SELECT_DETALLES)) {
            ps.setInt(1, idPedido);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalle(rs));
                }
            }
        }
        return detalles;
    }

    // =====================================================================
    // Mapeo ResultSet -> modelo
    // =====================================================================

    private Pedido mapearPedido(ResultSet rs) throws SQLException {
        Timestamp fecha = rs.getTimestamp("fecha");
        LocalDateTime momento = fecha != null ? fecha.toLocalDateTime() : LocalDateTime.now();
        return new Pedido(
            rs.getInt("id"),
            rs.getInt("mesa_id"),
            rs.getInt("usuario_id"),
            momento.toLocalDate(),
            momento.toLocalTime(),
            rs.getString("estado"),
            rs.getDouble("total")
        );
    }

    private DetallePedido mapearDetalle(ResultSet rs) throws SQLException {
        DetallePedido d = new DetallePedido();
        d.setId(rs.getInt("id"));
        d.setIdPedido(rs.getInt("pedido_id"));
        d.setIdProducto(rs.getInt("producto_id"));
        d.setCantidad(rs.getInt("cantidad"));
        d.setPrecioUnitario(rs.getDouble("precio_unitario"));
        return d;
    }
}
