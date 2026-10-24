package com.sv.grupo3.restaurante.exception;

/**
 * Se lanza cuando una operacion transaccional (pedido, cobro, ocupacion de mesa)
 * no pudo completarse y la capa de servicio ejecuto rollback.
 * Envuelve la SQLException original para que la capa de presentacion
 * no dependa del paquete java.sql.
 */
public class TransaccionException extends RuntimeException {

    public TransaccionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
