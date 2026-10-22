package com.example.ecommerce.domain.service;

import com.example.ecommerce.domain.exception.CorreoElectronicoDuplicadoException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;

/**
 * Servicio de dominio para la regla RN01: el correo electrónico es único.
 * La regla necesita mirar a TODOS los usuarios (el repositorio), por eso no cabe en la entidad Usuario.
 */
public class VerificadorDeCorreoUnico {

    private final UsuarioRepository usuarioRepository;

    public VerificadorDeCorreoUnico(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public void verificarQueNoEsteRegistrado(Email email) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new CorreoElectronicoDuplicadoException();
        }
    }
}
