package com.sv.grupo3.restaurante.model;

/**
 * Representa las mesas disponibles en el restaurante para la atencion de los clientes.
 */
public class Mesa {

    private int id;
    private int numeroMesa;
    private String zona;
    private String estado; // Ej: "LIBRE", "OCUPADA", "RESERVADA"

    public Mesa() {
    }

    public Mesa(int id, int numeroMesa, String zona, String estado) {
        this.id = id;
        this.numeroMesa = numeroMesa;
        this.zona = zona;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Mesa{" +
                "id=" + id +
                ", numeroMesa=" + numeroMesa +
                ", zona='" + zona + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
