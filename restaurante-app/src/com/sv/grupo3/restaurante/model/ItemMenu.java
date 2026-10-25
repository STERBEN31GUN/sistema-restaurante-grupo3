package com.sv.grupo3.restaurante.model;

/**
 * Clase base del catalogo. Platillo y Bebida heredan de ella.
 *
 * Los atributos id, nombre y precio son los que persiste ProductoRepository (JDBC).
 * descripcion, existencias y estado se conservan para la logica de negocio en memoria
 * (validacion de venta, menu disponible y vistas de consola) y conservan las validaciones
 * de encapsulamiento del Avance 2.
 */
public abstract class ItemMenu {
    protected int id;
    protected String nombre;
    protected double precio;
    private String descripcion;
    private int existencias;
    private String estado = "ACTIVO";

    public ItemMenu() {}

    public ItemMenu(int id, String nombre, double precio) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
    }

    public ItemMenu(int id, String nombre, String descripcion, double precio, int existencias, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        setPrecio(precio);
        setExistencias(existencias);
        this.estado = estado;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) {
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public int getExistencias() { return existencias; }
    public void setExistencias(int existencias) {
        if (existencias < 0) throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        this.existencias = existencias;
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    /** Resumen de una linea del item (usado por el hilo de cocina y los listados). */
    public abstract String obtenerDetalles();

    /** Instruccion de preparacion segun el tipo de item (polimorfismo). */
    public abstract void mostrarDetallesPreparacion();
}
