package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.domain.entity.Usuario;
import org.springframework.stereotype.Service;

/**
 * Inicio de sesión: verifica credenciales y entrega un token.
 */
@Service
public class IniciarSesionUseCase {

    private final AutenticarUsuarioUseCase autenticarUsuarioUseCase;
    private final TokenService tokenService;

    public IniciarSesionUseCase(AutenticarUsuarioUseCase autenticarUsuarioUseCase, TokenService tokenService) {
        this.autenticarUsuarioUseCase = autenticarUsuarioUseCase;
        this.tokenService = tokenService;
    }

    public SesionIniciada ejecutar(String email, String contrasenaPlano) {
        Usuario usuario = autenticarUsuarioUseCase.ejecutar(email, contrasenaPlano);
        return new SesionIniciada(tokenService.generar(usuario), usuario);
    }
}
