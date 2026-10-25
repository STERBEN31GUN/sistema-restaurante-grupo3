package com.sv.grupo3.restaurante.dto;

import java.time.LocalDateTime;

public class ComandaDTO {
    private Long id;
    private Long mesaId;
    private String estado; // PENDIENTE, EN_PREPARACION, LISTO
    private LocalDateTime fechaHora;

    public ComandaDTO() {}

    public ComandaDTO(Long id, Long mesaId, String estado, LocalDateTime fechaHora) {
        this.id = id;
        this.mesaId = mesaId;
        this.estado = estado;
        this.fechaHora = fechaHora;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMesaId() { return mesaId; }
    public void setMesaId(Long mesaId) { this.mesaId = mesaId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
}