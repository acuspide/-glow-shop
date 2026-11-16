package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Marca;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MarcaRepositoryEnMemoriaTest {

    @Test
    void listarTodasDebeRetornarlasOrdenadasPorNombre() {

        // Arrange
        MarcaRepositoryEnMemoria repository = new MarcaRepositoryEnMemoria();
        repository.guardar(Marca.crear("x-1", "Rostro"));
        repository.guardar(Marca.crear("x-2", "Labios"));
        repository.guardar(Marca.crear("x-3", "Ojos"));

        // Act
        List<Marca> resultado = repository.listarTodas();

        // Assert
        assertEquals(List.of("Labios", "Ojos", "Rostro"),
                resultado.stream().map(Marca::getNombre).toList());
    }

    @Test
    void listarTodasDebeRetornarUnaListaVaciaSiNoHayNinguna() {

        // Act y Assert
        assertTrue(new MarcaRepositoryEnMemoria().listarTodas().isEmpty());
    }
}
