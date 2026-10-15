package com.sv.grupo3.restaurante.model;

import java.io.Serializable;

public abstract class ItemMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulamiento estricto (Tarea de Ashley)
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int existencias;
    private String estado;

    public ItemMenu() {}

    public ItemMenu(int id, String nombre, String descripcion, double precio, int existencias, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        setPrecio(precio); // Usa la validación
        setExistencias(existencias);
        this.estado = estado;
    }

    // Método Abstracto para aplicar Polimorfismo
    public abstract void mostrarDetallesPreparacion();

    // Getters y Setters con validaciones
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { 
        if (precio < 0) throw new IllegalArgumentException("El precio no puede ser negativo.");
        this.precio = precio; 
    }

    public int getExistencias() { return existencias; }
    public void setExistencias(int existencias) { 
        if (existencias < 0) throw new IllegalArgumentException("Las existencias no pueden ser negativas.");
        this.existencias = existencias; 
    }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
