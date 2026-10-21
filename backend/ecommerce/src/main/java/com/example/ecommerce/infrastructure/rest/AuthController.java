package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.dto.request.LoginRequest;
import com.example.ecommerce.application.dto.response.LoginResponse;
import com.example.ecommerce.application.usecase.CerrarSesionUseCase;
import com.example.ecommerce.application.usecase.IniciarSesionUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final IniciarSesionUseCase iniciarSesionUseCase;
    private final CerrarSesionUseCase cerrarSesionUseCase;
    private final UsuarioMapper mapper;

    public AuthController(IniciarSesionUseCase iniciarSesionUseCase,
                          CerrarSesionUseCase cerrarSesionUseCase,
                          UsuarioMapper mapper) {
        this.iniciarSesionUseCase = iniciarSesionUseCase;
        this.cerrarSesionUseCase = cerrarSesionUseCase;
        this.mapper = mapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {

        SesionIniciada sesion = iniciarSesionUseCase.ejecutar(
                request.email(),
                request.contrasena()
        );

        return ResponseEntity.ok(mapper.toLoginResponse(sesion));
    }

    // CU-03: cierra la sesión del token enviado. Requiere "Authorization: Bearer <token>".
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        cerrarSesionUseCase.ejecutar(BearerToken.extraer(autorizacion));
        return ResponseEntity.noContent().build(); // 204
    }
}
