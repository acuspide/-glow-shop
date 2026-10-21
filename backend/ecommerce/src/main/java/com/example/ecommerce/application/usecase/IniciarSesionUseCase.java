package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.domain.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Inicio de sesión: verifica credenciales y entrega un token.
 */
@Service
@RequiredArgsConstructor
public class IniciarSesionUseCase {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public SesionIniciada ejecutar(String email, String contrasenaPlano) {
        Usuario usuario = autenticarUsuarioUseCase.ejecutar(email, contrasenaPlano);
        return new SesionIniciada(tokenService.generar(usuario), usuario);
    }
}
