package com.sv.grupo3.restaurante.repository;

import com.sv.grupo3.restaurante.dto.ReporteVentaDTO;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReporteVentaRepositoryJDBC {

    private final DataSource dataSource;

    public ReporteVentaRepositoryJDBC(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    // Obtener todas las ventas crudas desde la base de datos usando JDBC seguro
    public List<ReporteVentaDTO> obtenerReportesCrudos() {
        List<ReporteVentaDTO> lista = new ArrayList<>();
        String sql = "SELECT fecha, turno, plato_mas_vendido, total_ventas FROM reportes_venta";

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                LocalDate fecha = rs.getDate("fecha").toLocalDate();
                String turno = rs.getString("turno");
                String plato = rs.getString("plato_mas_vendido");
                double total = rs.getDouble("total_ventas");

                lista.add(new ReporteVentaDTO(fecha, turno, plato, total));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Analítica usando Java Streams: Ventas totales agrupadas por Día
    public Map<LocalDate, Double> calcularVentasPorDia() {
        List<ReporteVentaDTO> ventas = obtenerReportesCrudos();
        
        return ventas.stream()
                .collect(Collectors.groupingBy(
                        ReporteVentaDTO::getFecha,
                        Collectors.summingDouble(ReporteVentaDTO::getTotalVentas)
                ));
    }

    // Analítica usando Java Streams: Ventas totales agrupadas por Turno
    public Map<String, Double> calcularVentasPorTurno() {
        List<ReporteVentaDTO> ventas = obtenerReportesCrudos();
        
        return ventas.stream()
                .collect(Collectors.groupingBy(
                        ReporteVentaDTO::getTurno,
                        Collectors.summingDouble(ReporteVentaDTO::getTotalVentas)
                ));
    }
}