package com.sv.grupo3.restaurante.model;

public class Bebida extends ItemMenu {
    private static final long serialVersionUID = 1L;
    private boolean contieneAlcohol;

    public Bebida() { super(); }

    public Bebida(int id, String nombre, String descripcion, double precio, int existencias, String estado, boolean contieneAlcohol) {
        super(id, nombre, descripcion, precio, existencias, estado);
        this.contieneAlcohol = contieneAlcohol;
    }

    public boolean isContieneAlcohol() { return contieneAlcohol; }
    public void setContieneAlcohol(boolean contieneAlcohol) { this.contieneAlcohol = contieneAlcohol; }

    @Override
    public void mostrarDetallesPreparacion() {
        System.out.println("Bebida: " + getNombre());
        if(contieneAlcohol) {
            System.out.println("-> Instrucción: Enviar ticket al Bartender (Requiere validación de edad).");
        } else {
            System.out.println("-> Instrucción: Enviar ticket a la barra general de bebidas.");
        }
    }
}
