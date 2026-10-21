package com.example.ecommerce.domain.entity;

import com.example.ecommerce.domain.exception.CodigoRecuperacionInvalidoException;

import java.time.LocalDateTime;
import java.util.function.BiPredicate;

/**
 * Código de un solo uso para recuperar la contraseña.
 *
 * Reglas:
 *  - vence 15 minutos después de generarse;
 *  - solo se puede usar una vez;
 *  - después de 5 intentos fallidos queda inválido (evita adivinarlo probando números).
 *
 * Solo guarda el hash del código, nunca el código en texto plano.
 */
public class CodigoRecuperacion {

    public static final int MINUTOS_VIGENCIA = 15;
    public static final int MAXIMO_INTENTOS = 5;

    private final String usuarioId;
    private final String codigoHash;
    private final LocalDateTime expiraEn;
    private int intentosFallidos;
    private boolean usado;

    private CodigoRecuperacion(String usuarioId, String codigoHash, LocalDateTime expiraEn,
                               int intentosFallidos, boolean usado) {
        this.usuarioId = usuarioId;
        this.codigoHash = codigoHash;
        this.expiraEn = expiraEn;
        this.intentosFallidos = intentosFallidos;
        this.usado = usado;
    }

    public static CodigoRecuperacion generar(String usuarioId, String codigoHash, LocalDateTime ahora) {
        return new CodigoRecuperacion(usuarioId, codigoHash, ahora.plusMinutes(MINUTOS_VIGENCIA), 0, false);
    }

    /** Solo para el adaptador de persistencia. */
    public static CodigoRecuperacion reconstruir(String usuarioId, String codigoHash, LocalDateTime expiraEn,
                                                 int intentosFallidos, boolean usado) {
        return new CodigoRecuperacion(usuarioId, codigoHash, expiraEn, intentosFallidos, usado);
    }

    /**
     * Usa el código: si es válido queda marcado como usado; si no, lanza excepción.
     * Un código incorrecto suma un intento fallido antes de lanzar la excepción.
     *
     * @param coincideConHash compara el código escrito con el hash guardado
     *                        (lo aporta quien sabe calcular hashes, sin que el dominio dependa de él).
     */
    public void usar(String codigoIngresado, BiPredicate<String, String> coincideConHash, LocalDateTime ahora) {
        verificarQueSePuedeUsar(ahora);
        if (codigoIngresado == null || !coincideConHash.test(codigoIngresado, codigoHash)) {
            intentosFallidos++;
            throw new CodigoRecuperacionInvalidoException();
        }
        this.usado = true;
    }

    private void verificarQueSePuedeUsar(LocalDateTime ahora) {
        if (usado || intentosFallidos >= MAXIMO_INTENTOS || !ahora.isBefore(expiraEn)) {
            throw new CodigoRecuperacionInvalidoException();
        }
    }

    public String getUsuarioId() { return usuarioId; }
    public String getCodigoHash() { return codigoHash; }
    public LocalDateTime getExpiraEn() { return expiraEn; }
    public int getIntentosFallidos() { return intentosFallidos; }
    public boolean isUsado() { return usado; }
}
