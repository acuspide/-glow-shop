package com.example.ecommerce.domain.repository;

import java.util.List;

/**
 * Resultado paginado propio del dominio. No usa clases de Spring: el adaptador de persistencia
 * convierte el Page de Spring Data en esta clase.
 */
public record Pagina<T>(List<T> contenido, long totalElementos, int totalPaginas, int numero, int tamano) {
}
