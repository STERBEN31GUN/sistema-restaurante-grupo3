package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.model.Bebida;
import com.sv.grupo3.restaurante.model.ItemMenu;
import com.sv.grupo3.restaurante.model.Platillo;
import com.sv.grupo3.restaurante.dao.ProductoDAO;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductoRepository implements ProductoDAO {

    private final DataSource dataSource;

    private static final String SQL_SELECT_ALL = "SELECT id, nombre, precio, tipo FROM productos";
    private static final String SQL_SELECT_BY_ID = "SELECT id, nombre, precio, tipo FROM productos WHERE id = ?";
    private static final String SQL_INSERT = "INSERT INTO productos (nombre, precio, tipo) VALUES (?, ?, ?)";

    public ProductoRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<ItemMenu> obtenerTodos() {
        List<ItemMenu> lista = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_ALL);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                lista.add(mapearItemMenu(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar productos: " + e.getMessage());
        }
        return lista;
    }

    @Override
    public ItemMenu buscarPorId(int id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_SELECT_BY_ID)) {
             
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearItemMenu(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error buscando producto: " + e.getMessage());
        }
        return null;
    }

    @Override
    public boolean guardar(ItemMenu item) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(SQL_INSERT)) {
             
            ps.setString(1, item.getNombre());
            ps.setDouble(2, item.getPrecio());
            
            if (item instanceof Bebida) {
                ps.setString(3, "BEBIDA");
            } else {
                ps.setString(3, "PLATILLO");
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            return false;
        }
    }

    private ItemMenu mapearItemMenu(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nombre = rs.getString("nombre");
        double precio = rs.getDouble("precio");
        String tipo = rs.getString("tipo");

        if ("BEBIDA".equalsIgnoreCase(tipo)) {
            return new Bebida(id, nombre, precio);
        } else {
            return new Platillo(id, nombre, precio);
        }
    }
}