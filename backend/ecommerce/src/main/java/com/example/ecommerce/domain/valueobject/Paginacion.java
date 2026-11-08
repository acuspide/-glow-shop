package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.exception.ParametroInvalidoException;

/**
 * Página que se pide en una búsqueda paginada: la página empieza en 0 y el tamaño va de 1 a 50.
 */
public record Paginacion(int pagina, int tamano) {

    public static final int TAMANO_MAXIMO = 50;

    public Paginacion {
        if (pagina < 0) {
            throw new ParametroInvalidoException("La página no puede ser negativa");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new ParametroInvalidoException("El tamaño de la página debe estar entre 1 y " + TAMANO_MAXIMO);
        }
    }
}
