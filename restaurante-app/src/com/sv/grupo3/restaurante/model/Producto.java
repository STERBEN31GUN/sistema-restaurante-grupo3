package com.sv.grupo3.restaurante.model;

/**
 * Representa los elementos del menu ofertados (platillos y bebidas).
 * Entidad definida en el punto 11 del avance de proyecto.
 */
public class Producto {

    private int id;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double precio;
    private int existencias;
    private String estado; // Ej: "ACTIVO", "INACTIVO", "AGOTADO"

    public Producto() {
    }

    public Producto(int id, String nombre, String descripcion, String categoria,
                     double precio, int existencias, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precio = precio;
        this.existencias = existencias;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getExistencias() {
        return existencias;
    }

    public void setExistencias(int existencias) {
        this.existencias = existencias;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Producto{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", categoria='" + categoria + '\'' +
                ", precio=" + precio +
                ", existencias=" + existencias +
                ", estado='" + estado + '\'' +
                '}';
    }
}
