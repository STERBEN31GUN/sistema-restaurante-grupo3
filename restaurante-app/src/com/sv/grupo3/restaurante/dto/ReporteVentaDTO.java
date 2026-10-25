package com.sv.grupo3.restaurante.dto;

import java.time.LocalDate;

public class ReporteVentaDTO {
    private LocalDate fecha;
    private String turno;
    private String platoMasVendido;
    private double totalVentas;

    public ReporteVentaDTO() {}

    public ReporteVentaDTO(LocalDate fecha, String turno, String platoMasVendido, double totalVentas) {
        this.fecha = fecha;
        this.turno = turno;
        this.platoMasVendido = platoMasVendido;
        this.totalVentas = totalVentas;
    }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public String getTurno() { return turno; }
    public void setTurno(String turno) { this.turno = turno; }

    public String getPlatoMasVendido() { return platoMasVendido; }
    public void setPlatoMasVendido(String platoMasVendido) { this.platoMasVendido = platoMasVendido; }

    public double getTotalVentas() { return totalVentas; }
    public void setTotalVentas(double totalVentas) { this.totalVentas = totalVentas; }
}