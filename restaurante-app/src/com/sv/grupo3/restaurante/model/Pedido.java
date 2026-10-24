package com.sv.grupo3.restaurante.model;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Registro general de la orden realizada para una mesa en una fecha y hora especifica.
 */
public class Pedido {

    private int id;
    private int idMesa;      // FK mesas.id (persistencia JDBC)
    private int idUsuario;   // FK usuarios.id: mesero que registra la orden
    private int numeroMesa;
    private String zona;
    private LocalDate fecha;
    private LocalTime hora;
    private String estado; // Ej: "PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO", "PAGADO"
    private double total;
    private List<DetallePedido> detalles;

    public Pedido() {
        this.detalles = new ArrayList<>();
    }

    public Pedido(int id, int numeroMesa, String zona, LocalDate fecha, LocalTime hora, String estado) {
        this.id = id;
        this.numeroMesa = numeroMesa;
        this.zona = zona;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.total = 0.0;
        this.detalles = new ArrayList<>();
    }

    /**
     * Constructor usado por la capa de persistencia JDBC: el pedido queda
     * ligado a la mesa y al usuario por sus identificadores en la base de datos.
     */
    public Pedido(int id, int idMesa, int idUsuario, LocalDate fecha, LocalTime hora, String estado, double total) {
        this.id = id;
        this.idMesa = idMesa;
        this.idUsuario = idUsuario;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.total = total;
        this.detalles = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdMesa() {
        return idMesa;
    }

    public void setIdMesa(int idMesa) {
        this.idMesa = idMesa;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getNumeroMesa() {
        return numeroMesa;
    }

    public void setNumeroMesa(int numeroMesa) {
        this.numeroMesa = numeroMesa;
    }

    public String getZona() {
        return zona;
    }

    public void setZona(String zona) {
        this.zona = zona;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetallePedido> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedido> detalles) {
        this.detalles = detalles;
    }

    public void agregarDetalle(DetallePedido detalle) {
        this.detalles.add(detalle);
    }

    /** Suma de subtotales de los detalles cargados en memoria. */
    public double calcularTotal() {
        double suma = 0.0;
        for (DetallePedido d : detalles) {
            suma += d.getSubtotal();
        }
        return suma;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", idMesa=" + idMesa +
                ", idUsuario=" + idUsuario +
                ", numeroMesa=" + numeroMesa +
                ", zona='" + zona + '\'' +
                ", fecha=" + fecha +
                ", hora=" + hora +
                ", estado='" + estado + '\'' +
                ", total=" + total +
                ", detalles=" + detalles.size() +
                '}';
    }
}
