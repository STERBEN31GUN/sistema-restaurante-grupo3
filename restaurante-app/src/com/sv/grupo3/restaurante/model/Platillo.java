package com.sv.grupo3.restaurante.model;

public class Platillo extends ItemMenu {

    public Platillo() {
        super();
    }

    public Platillo(int id, String nombre, double precio) {
        super(id, nombre, precio);
    }

    @Override
    public String obtenerDetalles() {
        return "Platillo: " + getNombre() + " - $" + getPrecio();
    }
}