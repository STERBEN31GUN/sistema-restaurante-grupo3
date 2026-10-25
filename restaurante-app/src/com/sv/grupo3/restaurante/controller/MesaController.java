package com.sv.grupo3.restaurante.controller;

import com.sv.grupo3.restaurante.model.Mesa;
import com.sv.grupo3.restaurante.service.MesaService;

import java.util.List;

/**
 * Controlador del modulo de mesas. Valida la entrada de la vista y delega en
 * MesaService, que es quien abre y confirma (o revierte) la transaccion.
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 */
public class MesaController {

    private final MesaService mesaService;

    public MesaController(MesaService mesaService) {
        this.mesaService = mesaService;
    }

    public List<Mesa> listarMesas() {
        return mesaService.listarTodas();
    }

    public List<Mesa> listarMesasDisponibles() {
        return mesaService.listarDisponibles();
    }

    public Mesa consultarMesa(int idMesa) {
        validarId(idMesa);
        return mesaService.buscarPorId(idMesa);
    }

    public Mesa registrarMesa(int numeroMesa, String zona) {
        return mesaService.registrarMesa(numeroMesa, zona);
    }

    public void ocuparMesa(int idMesa) {
        validarId(idMesa);
        mesaService.ocuparMesa(idMesa);
    }

    public void liberarMesa(int idMesa) {
        validarId(idMesa);
        mesaService.liberarMesa(idMesa);
    }

    private void validarId(int idMesa) {
        if (idMesa <= 0) {
            throw new IllegalArgumentException("El ID de mesa debe ser un entero positivo.");
        }
    }
}
