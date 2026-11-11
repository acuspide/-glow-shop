package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Categoria;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CategoriaRepositoryEnMemoriaTest {

    @Test
    void listarTodasDebeRetornarlasOrdenadasPorNombre() {

        // Arrange
        CategoriaRepositoryEnMemoria repository = new CategoriaRepositoryEnMemoria();
        repository.guardar(Categoria.crear("x-1", "Rostro"));
        repository.guardar(Categoria.crear("x-2", "Labios"));
        repository.guardar(Categoria.crear("x-3", "Ojos"));

        // Act
        List<Categoria> resultado = repository.listarTodas();

        // Assert
        assertEquals(List.of("Labios", "Ojos", "Rostro"),
                resultado.stream().map(Categoria::getNombre).toList());
    }

    @Test
    void listarTodasDebeRetornarUnaListaVaciaSiNoHayNinguna() {

        // Act y Assert
        assertTrue(new CategoriaRepositoryEnMemoria().listarTodas().isEmpty());
    }
}
