package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.PasswordHasher;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.CredencialesInvalidasException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AutenticarUsuarioUseCase {

    private final UsuarioRepository repository;
    private final PasswordHasher passwordHasher;

    public AutenticarUsuarioUseCase(UsuarioRepository repository, PasswordHasher passwordHasher) {
        this.repository = repository;
        this.passwordHasher = passwordHasher;
    }

    @Transactional(readOnly = true)
    public Usuario ejecutar(String email, String contrasenaPlano) {
        Email correo = new Email(email);

        Usuario usuario = repository.buscarPorEmail(correo)
                .orElseThrow(CredencialesInvalidasException::new);

        usuario.autenticar(contrasenaPlano, passwordHasher::verificar);

        return usuario;
    }
}
