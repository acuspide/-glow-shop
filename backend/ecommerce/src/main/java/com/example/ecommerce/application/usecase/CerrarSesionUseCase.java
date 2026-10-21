package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.TokenService;
import org.springframework.stereotype.Service;

/**
 * CU-03 Cerrar sesión: invalida el token del usuario para que no se pueda volver a usar,
 * aunque todavía no haya expirado.
 */
@Service
public class CerrarSesionUseCase {

    private final TokenService tokenService;

    public CerrarSesionUseCase(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void ejecutar(String token) {
        tokenService.revocar(token);
    }
}
