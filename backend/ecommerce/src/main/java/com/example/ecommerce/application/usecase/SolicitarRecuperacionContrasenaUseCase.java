package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.NotificadorCorreo;
import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.CodigoRecuperacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.CodigoRecuperacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * CU-04 Recuperar contraseña (paso 1): genera un código de 6 dígitos y lo envía al correo.
 *
 * Si el correo no existe o la cuenta está desactivada, no hace nada y tampoco lo avisa:
 * así nadie puede usar este servicio para averiguar qué correos están registrados.
 */
@Service
@RequiredArgsConstructor
public class SolicitarRecuperacionContrasenaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final CodigoRecuperacionRepository codigoRepository;
    private final PasswordHasher passwordHasher;
    private final NotificadorCorreo notificador;
    private final SecureRandom aleatorio = new SecureRandom();

    @Transactional
    public void ejecutar(String email) {
        usuarioRepository.buscarPorEmail(new Email(email))
                .filter(Usuario::isActivo)
                .ifPresent(this::enviarCodigo);
    }

    private void enviarCodigo(Usuario usuario) {
        String codigo = String.format("%06d", aleatorio.nextInt(1_000_000));
        CodigoRecuperacion recuperacion = CodigoRecuperacion.generar(
                usuario.getId(), passwordHasher.hash(codigo), LocalDateTime.now());

        codigoRepository.guardar(recuperacion);
        notificador.enviarCodigoRecuperacion(usuario.getEmail(), codigo, CodigoRecuperacion.MINUTOS_VIGENCIA);
    }
}
