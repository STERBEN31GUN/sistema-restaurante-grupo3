package com.sv.grupo3.restaurante.service;

import com.sv.grupo3.restaurante.exception.RecursoNoEncontradoException;
import com.sv.grupo3.restaurante.exception.TransaccionException;
import com.sv.grupo3.restaurante.model.Mesa;
import com.sv.grupo3.restaurante.repository.MesaRepositoryJDBC;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Logica de negocio del modulo de mesas: consulta, ocupacion y liberacion.
 *
 * Ocupar o liberar una mesa se ejecuta dentro de una transaccion JDBC
 * (setAutoCommit(false) -> SELECT ... FOR UPDATE -> UPDATE -> commit) para que dos
 * meseros que intenten tomar la misma mesa al mismo tiempo no la asignen dos veces.
 * Las sobrecargas que reciben una Connection permiten que PedidoService incluya el
 * cambio de estado de la mesa dentro de la transaccion del pedido.
 *
 * Desarrollado por: Wilfredo Antonio Martinez Zometa
 * Rol: Desarrollador Fullstack / Integrador
 */
public class MesaService {

    public static final String ESTADO_DISPONIBLE = "DISPONIBLE";
    public static final String ESTADO_OCUPADA = "OCUPADA";
    /** Valor por defecto que asigna schema.sql; se interpreta como disponible. */
    private static final String ESTADO_LIBRE = "LIBRE";

    private final DataSource dataSource;
    private final MesaRepositoryJDBC mesaRepositorio;

    public MesaService(DataSource dataSource, MesaRepositoryJDBC mesaRepositorio) {
        this.dataSource = dataSource;
        this.mesaRepositorio = mesaRepositorio;
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------

    public List<Mesa> listarTodas() {
        return mesaRepositorio.listarTodos();
    }

    public List<Mesa> listarDisponibles() {
        return listarTodas().stream()
                .filter(m -> esDisponible(m.getEstado()))
                .collect(Collectors.toList());
    }

    public Mesa buscarPorId(int idMesa) {
        Mesa mesa = mesaRepositorio.buscarPorId(idMesa);
        if (mesa == null) {
            throw new RecursoNoEncontradoException("No existe una mesa con el ID: " + idMesa);
        }
        return mesa;
    }

    public Mesa registrarMesa(int numeroMesa, String zona) {
        if (numeroMesa <= 0) {
            throw new IllegalArgumentException("El numero de mesa debe ser mayor a cero.");
        }
        if (zona == null || zona.trim().isEmpty()) {
            throw new IllegalArgumentException("La zona de la mesa es obligatoria.");
        }
        return mesaRepositorio.guardar(new Mesa(0, numeroMesa, zona.trim(), ESTADO_DISPONIBLE));
    }

    // ------------------------------------------------------------------
    // Operaciones transaccionales independientes (abren su propia transaccion)
    // ------------------------------------------------------------------

    /** Marca la mesa como OCUPADA. Falla si no existe o si ya estaba ocupada. */
    public void ocuparMesa(int idMesa) {
        cambiarEstadoEnTransaccion(idMesa, true);
    }

    /** Marca la mesa como DISPONIBLE al cerrar la cuenta. */
    public void liberarMesa(int idMesa) {
        cambiarEstadoEnTransaccion(idMesa, false);
    }

    private void cambiarEstadoEnTransaccion(int idMesa, boolean ocupar) {
        Connection con = null;
        try {
            con = dataSource.getConnection();
            con.setAutoCommit(false); // inicio de la transaccion
            if (ocupar) {
                ocuparMesa(con, idMesa);
            } else {
                liberarMesa(con, idMesa);
            }
            con.commit();             // confirma el cambio de estado
        } catch (SQLException e) {
            revertir(con);
            throw new TransaccionException(
                "No se pudo " + (ocupar ? "ocupar" : "liberar") + " la mesa " + idMesa
                + "; se revirtio la operacion.", e);
        } catch (RuntimeException e) {
            revertir(con);            // regla de negocio incumplida: tambien se revierte
            throw e;
        } finally {
            cerrar(con);
        }
    }

    // ------------------------------------------------------------------
    // Operaciones que participan en una transaccion externa
    // ------------------------------------------------------------------

    /**
     * Bloquea la fila de la mesa, valida que este disponible y la marca como OCUPADA.
     * No hace commit: lo decide quien abrio la conexion.
     */
    public void ocuparMesa(Connection con, int idMesa) throws SQLException {
        String estadoActual = mesaRepositorio.leerEstadoConBloqueo(con, idMesa);
        if (estadoActual == null) {
            throw new RecursoNoEncontradoException("No existe una mesa con el ID: " + idMesa);
        }
        if (!esDisponible(estadoActual)) {
            throw new IllegalStateException("La mesa " + idMesa + " ya se encuentra " + estadoActual + ".");
        }
        mesaRepositorio.cambiarEstado(con, idMesa, ESTADO_OCUPADA);
    }

    /** Libera la mesa dentro de la transaccion recibida. No hace commit. */
    public void liberarMesa(Connection con, int idMesa) throws SQLException {
        String estadoActual = mesaRepositorio.leerEstadoConBloqueo(con, idMesa);
        if (estadoActual == null) {
            throw new RecursoNoEncontradoException("No existe una mesa con el ID: " + idMesa);
        }
        mesaRepositorio.cambiarEstado(con, idMesa, ESTADO_DISPONIBLE);
    }

    // ------------------------------------------------------------------
    // Utilitarios
    // ------------------------------------------------------------------

    private boolean esDisponible(String estado) {
        return ESTADO_DISPONIBLE.equalsIgnoreCase(estado) || ESTADO_LIBRE.equalsIgnoreCase(estado);
    }

    private void revertir(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.rollback();
        } catch (SQLException e) {
            System.err.println("[MesaService] No se pudo ejecutar rollback: " + e.getMessage());
        }
    }

    private void cerrar(Connection con) {
        if (con == null) {
            return;
        }
        try {
            con.setAutoCommit(true); // la conexion vuelve al pool en modo normal
            con.close();
        } catch (SQLException e) {
            System.err.println("[MesaService] No se pudo cerrar la conexion: " + e.getMessage());
        }
    }
}
