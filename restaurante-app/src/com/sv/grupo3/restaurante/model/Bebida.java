package com.sv.grupo3.restaurante.model;

public class Bebida extends ItemMenu {

    public Bebida() {
        super();
    }

    public Bebida(int id, String nombre, double precio) {
        super(id, nombre, precio);
    }

    @Override
    public String obtenerDetalles() {
        return "Bebida: " + getNombre() + " - $" + getPrecio();
    }
}