package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.ContrasenaPlana;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-06 Actualizar perfil (contraseña): cambio directo, conociendo la contraseña actual.
 * Si el usuario la olvidó, usa CU-04 Recuperar contraseña.
 */
@Service
@RequiredArgsConstructor
public class CambiarContrasenaUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;

    @Transactional
    public void ejecutar(String usuarioId, String contrasenaActual, String nuevaContrasenaPlano) {
        ContrasenaPlana nuevaContrasena = new ContrasenaPlana(nuevaContrasenaPlano); // valida que sea segura
        Usuario usuario = repository.obtenerPorId(usuarioId).orElseThrow();         // no existe -> 404

        usuario.cambiarContrasena(contrasenaActual, passwordHasher.hash(nuevaContrasena.valor()),
                passwordHasher::verificar);
        repository.guardar(usuario);
    }
}
