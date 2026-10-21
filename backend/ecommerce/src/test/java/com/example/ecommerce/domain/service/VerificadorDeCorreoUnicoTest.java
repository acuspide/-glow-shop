package com.example.ecommerce.domain.service;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VerificadorDeCorreoUnicoTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final VerificadorDeCorreoUnico verificador = new VerificadorDeCorreoUnico(repository);

    @Test
    void unCorreoNuevoPasaLaVerificacion() {
        assertDoesNotThrow(() -> verificador.verificarQueNoEsteRegistrado(new Email("nueva@correo.com")));
    }

    @Test
    void unCorreoYaRegistradoNoPasaLaVerificacion() {
        repository.guardar(Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash",
                DatosPrueba.TELEFONO, DatosPrueba.FECHA));

        assertThrows(CorreoElectronicoDuplicadoException.class,
                () -> verificador.verificarQueNoEsteRegistrado(new Email("ANA@correo.com")));
    }
}
