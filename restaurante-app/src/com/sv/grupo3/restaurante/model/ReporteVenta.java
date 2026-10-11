package com.sv.grupo3.restaurante.model;

import java.time.LocalDate;

/**
 * Estructura para consolidar el control financiero y estadisticas
 * de ventas por dia o turno.
 */
public class ReporteVenta {

    private int id;
    private LocalDate fecha;
    private String turno; // Ej: "MANANA", "TARDE", "NOCHE"
    private double totalVendido;
    private String platilloMasVendido;

    public ReporteVenta() {
    }

    public ReporteVenta(int id, LocalDate fecha, String turno, double totalVendido, String platilloMasVendido) {
        this.id = id;
        this.fecha = fecha;
        this.turno = turno;
        this.totalVendido = totalVendido;
        this.platilloMasVendido = platilloMasVendido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public double getTotalVendido() {
        return totalVendido;
    }

    public void setTotalVendido(double totalVendido) {
        this.totalVendido = totalVendido;
    }

    public String getPlatilloMasVendido() {
        return platilloMasVendido;
    }

    public void setPlatilloMasVendido(String platilloMasVendido) {
        this.platilloMasVendido = platilloMasVendido;
    }

    @Override
    public String toString() {
        return "ReporteVenta{" +
                "id=" + id +
                ", fecha=" + fecha +
                ", turno='" + turno + '\'' +
                ", totalVendido=" + totalVendido +
                ", platilloMasVendido='" + platilloMasVendido + '\'' +
                '}';
    }
}
