package com.sv.grupo3.restaurante.model;

public class Platillo extends ItemMenu {
    private boolean requiereCoccion;

    public Platillo() {
        super();
    }

    public Platillo(int id, String nombre, double precio) {
        super(id, nombre, precio);
    }

    public Platillo(int id, String nombre, String descripcion, double precio, int existencias, String estado, boolean requiereCoccion) {
        super(id, nombre, descripcion, precio, existencias, estado);
        this.requiereCoccion = requiereCoccion;
    }

    public boolean isRequiereCoccion() { return requiereCoccion; }
    public void setRequiereCoccion(boolean requiereCoccion) { this.requiereCoccion = requiereCoccion; }

    @Override
    public String obtenerDetalles() {
        return "Platillo: " + getNombre() + " - $" + getPrecio();
    }

    @Override
    public void mostrarDetallesPreparacion() {
        System.out.println(obtenerDetalles());
        if (requiereCoccion) {
            System.out.println("-> Instrucción: Enviar a la estación caliente (Parrilla/Estufa).");
        } else {
            System.out.println("-> Instrucción: Enviar a la estación fría (Ensaladas/Postres).");
        }
    }
}
