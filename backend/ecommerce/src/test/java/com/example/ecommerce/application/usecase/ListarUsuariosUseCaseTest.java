package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FiltroUsuarios;
import com.example.ecommerce.domain.valueobject.RolUsuario;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ListarUsuariosUseCaseTest {

    private final UsuarioRepository repository = new UsuarioRepositoryEnMemoria();
    private final ListarUsuariosUseCase useCase = new ListarUsuariosUseCase(repository);

    @BeforeEach
    void guardarUsuarios() {
        repository.guardar(Usuario.administradorInicial("a-1", "Admin", new Email("admin@glow.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        repository.guardar(Usuario.registrarCliente("u-1", "Zoe", new Email("zoe@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        repository.guardar(Usuario.registrarVendedor("u-2", "Vera", new Email("vera@tienda.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA));
        repository.guardar(Usuario.reconstruir("u-3", "ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA, RolUsuario.CLIENTE, false));
    }

    private List<String> nombres(List<Usuario> usuarios) {
        return usuarios.stream().map(Usuario::getNombre).toList();
    }

    @Test
    void sinFiltrosDebeDevolverTodosOrdenadosPorNombre() {
        List<Usuario> usuarios = useCase.ejecutar("a-1", new FiltroUsuarios(null, null));

        assertEquals(List.of("Admin", "ana", "Vera", "Zoe"), nombres(usuarios));
    }

    @Test
    void debeFiltrarPorRolYEstado() {
        List<Usuario> clientesActivos = useCase.ejecutar("a-1", new FiltroUsuarios(RolUsuario.CLIENTE, true));

        assertEquals(List.of("Zoe"), nombres(clientesActivos));
    }

    @Test
    void unClienteNoPuedeListarUsuarios() {
        assertThrows(AccionSoloParaAdministradorException.class,
                () -> useCase.ejecutar("u-1", new FiltroUsuarios(null, null)));
    }

    @Test
    void unUsuarioQueNoExisteNoPuedeListarUsuarios() {
        assertThrows(AccionSoloParaAdministradorException.class,
                () -> useCase.ejecutar("no-existe", new FiltroUsuarios(null, null)));
    }
}
