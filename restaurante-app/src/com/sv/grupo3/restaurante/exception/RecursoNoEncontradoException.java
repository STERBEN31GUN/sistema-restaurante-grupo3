package com.sv.grupo3.restaurante.exception;

/**
 * Se lanza cuando se busca una entidad (producto, mesa, pedido, usuario, etc.)
 * que no existe en el repositorio.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
