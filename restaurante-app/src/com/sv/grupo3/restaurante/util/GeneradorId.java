package com.sv.grupo3.restaurante.util;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Genera identificadores incrementales en memoria para cada tipo de entidad.
 * En una version futura con base de datos, esto lo reemplazaria el
 * autoincremental / secuencia de la tabla.
 */
public class GeneradorId {

    private final AtomicInteger contador = new AtomicInteger(0);

    public int siguiente() {
        return contador.incrementAndGet();
    }
}
