package com.example.ecommerce.application.usecase;

import com.example.ecommerce.application.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * CU-03 Cerrar sesión: invalida el token del usuario para que no se pueda volver a usar,
 * aunque todavía no haya expirado.
 */
@Service
@RequiredArgsConstructor
public class CerrarSesionUseCase {

    private final TokenService tokenService;

    public void ejecutar(String token) {
        tokenService.revocar(token);
    }
}
