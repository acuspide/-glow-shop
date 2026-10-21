package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.CodigoRecuperacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CodigoRecuperacionInvalidoException;
import com.example.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import com.example.ecommerce.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * CU-04 Recuperar contraseña (paso 2): con el código recibido por correo, define una contraseña nueva.
 */
@Service
public class RestablecerContrasenaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordHasher passwordHasher;

    public RestablecerContrasenaUseCase(UsuarioRepository usuarioRepository,
                                        CodigoRecuperacionRepository codigoRepository,
                                        PasswordHasher passwordHasher) {
        this.usuarioRepository = usuarioRepository;
        this.codigoRepository = codigoRepository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(noRollbackFor = CodigoRecuperacionInvalidoException.class)
    public void ejecutar(String email, String codigo, String nuevaContrasenaPlano) {
        ContrasenaPlana nuevaContrasena = new ContrasenaPlana(nuevaContrasenaPlano); // el dominio valida

        // Correo inexistente o sin código pedido: mismo error que un código incorrecto.
        Usuario usuario = usuarioRepository.buscarPorEmail(new Email(email))
                .orElseThrow(CodigoRecuperacionInvalidoException::new);
        CodigoRecuperacion recuperacion = codigoRepository.obtenerPorUsuarioId(usuario.getId())
                .orElseThrow(CodigoRecuperacionInvalidoException::new);

        try {
            recuperacion.usar(codigo, passwordHasher::verificar, LocalDateTime.now());
        } finally {
            codigoRepository.guardar(recuperacion); // se guarda siempre: también los intentos fallidos
        }

        usuario.cambiarContrasena(passwordHasher.hash(nuevaContrasena.valor()));
        usuarioRepository.guardar(usuario);
    }
}
