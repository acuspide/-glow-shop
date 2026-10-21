package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

public class ConsultarPerfilUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final ConsultarPerfilUseCase useCase = new ConsultarPerfilUseCase(repository);

    @Test
    void debeDevolverLosDatosDelUsuario() {
        repository.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA));

        Usuario perfil = useCase.ejecutar("u-1");

        assertEquals("Ana", perfil.getNombre());
        assertEquals("ana@correo.com", perfil.getEmail().getValor());
    }

    @Test
    void siElUsuarioNoExisteDebeLanzarNoEncontrado() {
        assertThrows(NoSuchElementException.class, () -> useCase.ejecutar("no-existe"));
    }
}
