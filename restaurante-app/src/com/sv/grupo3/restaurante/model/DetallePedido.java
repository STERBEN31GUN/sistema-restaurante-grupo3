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
    private double precioUnitario; // precio vigente al momento de la comanda (detalles_pedido.precio_unitario)
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
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
        this.cantidad = cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public void setPrecioUnitario(double precioUnitario) {
        if (precioUnitario < 0) {
            throw new IllegalArgumentException("El precio unitario no puede ser negativo.");
        }
        this.precioUnitario = precioUnitario;
    }

    /** Importe de la linea: cantidad por precio unitario. */
    public double getSubtotal() {
        return cantidad * precioUnitario;
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
                ", precioUnitario=" + precioUnitario +
                ", observaciones='" + observaciones + '\'' +
                ", estadoPreparacion='" + estadoPreparacion + '\'' +
                '}';
    }
}
