package com.example.ecommerce.application.usecase;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.NotificadorCorreo;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CodigoRecuperacionInvalidoException;
import com.example.ecommerce.domain.exception.ContrasenaDebilException;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.CodigoRecuperacionRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.persistence.UsuarioRepositoryEnMemoria;
import com.example.ecommerce.infrastructure.security.PasswordHasherSha256;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** CU-04: pruebas de los dos pasos (solicitar el código y restablecer la contraseña). */
public class RecuperarContrasenaUseCaseTest {

    /** Doble de prueba: en vez de enviar el correo, guarda el último código. */
    private static class CorreoFalso implements NotificadorCorreo {
        String ultimoCodigo;
        int enviados;

        @Override
        public void enviarCodigoRecuperacion(Email destinatario, String codigo, int minutosVigencia) {
            this.ultimoCodigo = codigo;
            this.enviados++;
        }
    }

    private final UsuarioRepository usuarioRepository = new UsuarioRepositoryEnMemoria();
    private final CodigoRecuperacionRepository codigoRepository = new CodigoRecuperacionRepositoryEnMemoria();
    private final PasswordHasher passwordHasher = new PasswordHasherSha256();
    private final CorreoFalso correo = new CorreoFalso();

    private final SolicitarRecuperacionContrasenaUseCase solicitar =
            new SolicitarRecuperacionContrasenaUseCase(usuarioRepository, codigoRepository, passwordHasher, correo);
    private final RestablecerContrasenaUseCase restablecer =
            new RestablecerContrasenaUseCase(usuarioRepository, codigoRepository, passwordHasher);
    private final AutenticarUsuarioUseCase autenticar = new AutenticarUsuarioUseCase(usuarioRepository, passwordHasher);

    private Usuario ana;

    @BeforeEach
    void registrarUsuario() {
        ana = new RegistrarClienteUseCase(usuarioRepository, passwordHasher, new VerificadorDeCorreoUnico(usuarioRepository))
                .ejecutar("u-1", "Ana", "ana@correo.com", "Clave123", DatosPrueba.TELEFONO_TEXTO, DatosPrueba.FECHA_VALOR);
    }

    @Test
    void debeEnviarUnCodigoDe6DigitosAlCorreoRegistrado() {
        solicitar.ejecutar("ana@correo.com");

        assertEquals(1, correo.enviados);
        assertTrue(correo.ultimoCodigo.matches("\\d{6}"));
        assertNotEquals(correo.ultimoCodigo, codigoRepository.obtenerPorUsuarioId("u-1").orElseThrow().getCodigoHash());
    }

    @Test
    void noDebeEnviarNadaNiFallarSiElCorreoNoExiste() {
        assertDoesNotThrow(() -> solicitar.ejecutar("nadie@correo.com"));
        assertEquals(0, correo.enviados);
    }

    @Test
    void noDebeEnviarCodigoAUnaCuentaDesactivada() {
        ana.desactivar();

        solicitar.ejecutar("ana@correo.com");

        assertEquals(0, correo.enviados);
    }

    @Test
    void conElCodigoCorrectoDebeCambiarLaContrasena() {
        solicitar.ejecutar("ana@correo.com");

        restablecer.ejecutar("ana@correo.com", correo.ultimoCodigo, "NuevaClave9");

        assertEquals("u-1", autenticar.ejecutar("ana@correo.com", "NuevaClave9").getId());
        assertThrows(CredencialesInvalidasException.class, () -> autenticar.ejecutar("ana@correo.com", "Clave123"));
    }

    @Test
    void elCodigoSoloSePuedeUsarUnaVez() {
        solicitar.ejecutar("ana@correo.com");
        String codigo = correo.ultimoCodigo;
        restablecer.ejecutar("ana@correo.com", codigo, "NuevaClave9");

        assertThrows(CodigoRecuperacionInvalidoException.class,
                () -> restablecer.ejecutar("ana@correo.com", codigo, "OtraClave99"));
    }

    @Test
    void conUnCodigoIncorrectoNoDebeCambiarLaContrasena() {
        solicitar.ejecutar("ana@correo.com");
        String incorrecto = correo.ultimoCodigo.equals("000000") ? "111111" : "000000";

        assertThrows(CodigoRecuperacionInvalidoException.class,
                () -> restablecer.ejecutar("ana@correo.com", incorrecto, "NuevaClave9"));
        assertEquals("u-1", autenticar.ejecutar("ana@correo.com", "Clave123").getId());
    }

    @Test
    void despuesDe5IntentosFallidosNiElCodigoCorrectoSirve() {
        solicitar.ejecutar("ana@correo.com");
        String correcto = correo.ultimoCodigo;
        String incorrecto = correcto.equals("000000") ? "111111" : "000000";
        for (int i = 0; i < 5; i++) {
            assertThrows(CodigoRecuperacionInvalidoException.class,
                    () -> restablecer.ejecutar("ana@correo.com", incorrecto, "NuevaClave9"));
        }

        assertThrows(CodigoRecuperacionInvalidoException.class,
                () -> restablecer.ejecutar("ana@correo.com", correcto, "NuevaClave9"));
    }

    @Test
    void laNuevaContrasenaDebeSerSegura() {
        solicitar.ejecutar("ana@correo.com");

        assertThrows(ContrasenaDebilException.class,
                () -> restablecer.ejecutar("ana@correo.com", correo.ultimoCodigo, "debil"));
    }

    @Test
    void sinHaberPedidoCodigoNoSePuedeRestablecer() {
        assertThrows(CodigoRecuperacionInvalidoException.class,
                () -> restablecer.ejecutar("ana@correo.com", "123456", "NuevaClave9"));
    }
}
