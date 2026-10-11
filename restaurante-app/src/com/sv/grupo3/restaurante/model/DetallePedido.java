package com.sv.grupo3.restaurante.model;

/**
 * Representa cada item individual solicitado dentro de un pedido
 * (comidas o bebidas con sus notas).
 */
public class DetallePedido {

    private int id;
    private int idPedido;
    private int idProducto;
    private int cantidad;
    private String observaciones;
    private String estadoPreparacion; // Ej: "PENDIENTE", "EN_PREPARACION", "LISTO"

    public DetallePedido() {
    }

    public DetallePedido(int id, int idPedido, int idProducto, int cantidad,
                          String observaciones, String estadoPreparacion) {
        this.id = id;
        this.idPedido = idPedido;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.observaciones = observaciones;
        this.estadoPreparacion = estadoPreparacion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public String getEstadoPreparacion() {
        return estadoPreparacion;
    }

    public void setEstadoPreparacion(String estadoPreparacion) {
        this.estadoPreparacion = estadoPreparacion;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + id +
                ", idPedido=" + idPedido +
                ", idProducto=" + idProducto +
                ", cantidad=" + cantidad +
                ", observaciones='" + observaciones + '\'' +
                ", estadoPreparacion='" + estadoPreparacion + '\'' +
                '}';
    }
}
