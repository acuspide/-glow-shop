package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CalificacionAjenaException;
import com.example.ecommerce.domain.exception.CalificacionEliminadaException;
import com.example.ecommerce.domain.exception.ComentarioInvalidoException;
import com.example.ecommerce.domain.valueobject.Puntuacion;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Calificación (reseña) que un cliente deja sobre un artículo que recibió.
 *
 * Reglas que protege:
 *  - la puntuación va de 1 a 5 (Puntuacion);
 *  - el comentario es obligatorio y tiene máximo 500 caracteres;
 *  - guarda solo los ids del artículo y del cliente: son otros agregados;
 *  - se elimina de forma lógica (no se borra) y una eliminada ya no se modifica.
 *
 * Que el cliente haya recibido el artículo y que no lo haya calificado antes
 * lo verifica el servicio de dominio VerificadorDeCalificacion (necesita mirar otros datos).
 */
public class Calificacion {

    public static final int MAXIMO_CARACTERES_COMENTARIO = 500;

    private final String id;
    private final long articuloId;
    private final String clienteId;
    private Puntuacion puntuacion;
    private String comentario;
    private final LocalDateTime fecha;
    private boolean eliminada;

    private Calificacion(String id, long articuloId, String clienteId, Puntuacion puntuacion,
                         String comentario, LocalDateTime fecha, boolean eliminada) {
        this.id = id;
        this.articuloId = articuloId;
        this.clienteId = clienteId;
        this.puntuacion = puntuacion;
        this.comentario = comentario;
        this.fecha = fecha;
        this.eliminada = eliminada;
    }

    // Único punto de entrada para crear una calificación: valida ANTES de existir.
    public static Calificacion calificar(String id, long articuloId, Usuario cliente,
                                         Puntuacion puntuacion, String comentario, LocalDateTime fecha) {
        cliente.verificarQueEsClienteActivo();
        validarComentario(comentario);
        return new Calificacion(id, articuloId, cliente.getId(), puntuacion, comentario.trim(), fecha, false);
    }

    /** Solo para el adaptador de persistencia. */
    public static Calificacion reconstruir(String id, long articuloId, String clienteId, Puntuacion puntuacion,
                                           String comentario, LocalDateTime fecha, boolean eliminada) {
        return new Calificacion(id, articuloId, clienteId, puntuacion, comentario, fecha, eliminada);
    }

    /**
     * CU-92: el autor edita sus estrellas y su comentario.
     * La fecha original se conserva.
     */
    public void actualizarPor(Usuario cliente, Puntuacion nuevaPuntuacion, String nuevoComentario) {
        verificarQueEsDelAutor(cliente);
        validarComentario(nuevoComentario);
        this.puntuacion = nuevaPuntuacion;
        this.comentario = nuevoComentario.trim();
    }

    /**
     * CU-93: el autor retira su calificación. Es una baja lógica: deja de mostrarse y de contar
     * en el promedio, pero no se borra.
     */
    public void eliminarPor(Usuario cliente) {
        verificarQueEsDelAutor(cliente);
        this.eliminada = true;
    }

    private void verificarQueEsDelAutor(Usuario cliente) {
        if (eliminada) {
            throw new CalificacionEliminadaException();
        }
        cliente.verificarQueEsClienteActivo();
        if (!clienteId.equals(cliente.getId())) {
            throw new CalificacionAjenaException();
        }
    }

    private static void validarComentario(String comentario) {
        if (comentario == null || comentario.isBlank() || comentario.trim().length() > MAXIMO_CARACTERES_COMENTARIO) {
            throw new ComentarioInvalidoException();
        }
    }

    public String getId() { return id; }
    public long getArticuloId() { return articuloId; }
    public String getClienteId() { return clienteId; }
    public Puntuacion getPuntuacion() { return puntuacion; }
    public String getComentario() { return comentario; }
    public LocalDateTime getFecha() { return fecha; }
    public boolean isEliminada() { return eliminada; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Calificacion otra)) return false;
        return id.equals(otra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
