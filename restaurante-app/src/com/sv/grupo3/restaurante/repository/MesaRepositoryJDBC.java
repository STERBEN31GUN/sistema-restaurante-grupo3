package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dao.GenericDAO;
import com.sv.grupo3.restaurante.model.Mesa;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Repositorio JDBC para la entidad Mesa.
 * Estados permitidos: DISPONIBLE y OCUPADA.
 */
public class MesaRepositoryJDBC implements GenericDAO<Mesa> {

    private static final Set<String> ESTADOS_VALIDOS = Set.of("DISPONIBLE", "OCUPADA");

    private static final String SQL_INSERT =
        "INSERT INTO mesas (numero_mesa, zona, estado) VALUES (?, ?, ?)";
    private static final String SQL_SELECT_BY_ID =
        "SELECT id, numero_mesa, zona, estado FROM mesas WHERE id = ?";
    private static final String SQL_SELECT_ALL =
        "SELECT id, numero_mesa, zona, estado FROM mesas ORDER BY numero_mesa";
    private static final String SQL_SELECT_BY_ESTADO =
        "SELECT id, numero_mesa, zona, estado FROM mesas WHERE estado = ? ORDER BY numero_mesa";
    private static final String SQL_UPDATE =
        "UPDATE mesas SET numero_mesa = ?, zona = ?, estado = ? WHERE id = ?";
    private static final String SQL_UPDATE_ESTADO =
        "UPDATE mesas SET estado = ? WHERE id = ?";
    private static final String SQL_DELETE =
        "DELETE FROM mesas WHERE id = ?";

    private final DataSource dataSource;

    public MesaRepositoryJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Mesa guardar(Mesa mesa) {
        validarEstado(mesa.getEstado());
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, mesa.getNumeroMesa());
            ps.setString(2, mesa.getZona());
            ps.setString(3, mesa.getEstado());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    mesa.setId(keys.getInt(1));
                }
            }
            return mesa;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo guardar la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public Mesa buscarPorId(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ID)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapear(rs);
                }
            }
            return null; // igual que MesaRepository: null si no existe
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo buscar la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Mesa> listarTodos() {
        List<Mesa> mesas = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                mesas.add(mapear(rs));
            }
            return mesas;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo listar las mesas: " + e.getMessage(), e);
        }
    }

    public List<Mesa> listarPorEstado(String estado) {
        validarEstado(estado);
        List<Mesa> mesas = new ArrayList<>();
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_SELECT_BY_ESTADO)) {

            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    mesas.add(mapear(rs));
                }
            }
            return mesas;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo listar mesas por estado: " + e.getMessage(), e);
        }
    }

    @Override
    public Mesa actualizar(Mesa mesa) {
        validarEstado(mesa.getEstado());
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setInt(1, mesa.getNumeroMesa());
            ps.setString(2, mesa.getZona());
            ps.setString(3, mesa.getEstado());
            ps.setInt(4, mesa.getId());
            ps.executeUpdate();
            return mesa;
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo actualizar la mesa: " + e.getMessage(), e);
        }
    }

    /** Cambia solo el estado de una mesa (abrir o cerrar una cuenta). */
    public void cambiarEstado(int id, String nuevoEstado) {
        validarEstado(nuevoEstado);
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE_ESTADO)) {

            ps.setString(1, nuevoEstado);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo cambiar el estado de la mesa: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(int id) {
        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("[Error] No se pudo eliminar la mesa: " + e.getMessage(), e);
        }
    }

    private Mesa mapear(ResultSet rs) throws SQLException {
        return new Mesa(
            rs.getInt("id"),
            rs.getInt("numero_mesa"),
            rs.getString("zona"),
            rs.getString("estado")
        );
    }

    private void validarEstado(String estado) {
        if (estado == null || !ESTADOS_VALIDOS.contains(estado)) {
            throw new IllegalArgumentException(
                "Estado de mesa inválido: " + estado + ". Valores permitidos: " + ESTADOS_VALIDOS);
        }
    }
}