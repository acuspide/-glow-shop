package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.dto.request.LoginRequest;
import com.example.ecommerce.application.dto.request.RestablecerContrasenaRequest;
import com.example.ecommerce.application.dto.request.SolicitarRecuperacionContrasenaRequest;
import com.example.ecommerce.application.dto.response.LoginResponse;
import com.example.ecommerce.application.usecase.CerrarSesionUseCase;
import com.example.ecommerce.application.usecase.IniciarSesionUseCase;
import com.example.ecommerce.application.usecase.RestablecerContrasenaUseCase;
import com.example.ecommerce.application.usecase.SolicitarRecuperacionContrasenaUseCase;
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
    private final SolicitarRecuperacionContrasenaUseCase solicitarRecuperacionUseCase;
    private final RestablecerContrasenaUseCase restablecerContrasenaUseCase;
    private final UsuarioMapper mapper;

    public AuthController(IniciarSesionUseCase iniciarSesionUseCase,
                          CerrarSesionUseCase cerrarSesionUseCase,
                          SolicitarRecuperacionContrasenaUseCase solicitarRecuperacionUseCase,
                          RestablecerContrasenaUseCase restablecerContrasenaUseCase,
                          UsuarioMapper mapper) {
        this.iniciarSesionUseCase = iniciarSesionUseCase;
        this.cerrarSesionUseCase = cerrarSesionUseCase;
        this.solicitarRecuperacionUseCase = solicitarRecuperacionUseCase;
        this.restablecerContrasenaUseCase = restablecerContrasenaUseCase;
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

    // CU-04 (paso 1): pide el código de recuperación. Responde igual exista o no el correo.
    @PostMapping("/recuperar-contrasena")
    public ResponseEntity<Void> solicitarRecuperacion(
            @Valid @RequestBody SolicitarRecuperacionContrasenaRequest request) {

        solicitarRecuperacionUseCase.ejecutar(request.email());
        return ResponseEntity.accepted().build(); // 202
    }

    // CU-04 (paso 2): define la nueva contraseña usando el código recibido por correo.
    @PostMapping("/restablecer-contrasena")
    public ResponseEntity<Void> restablecerContrasena(
            @Valid @RequestBody RestablecerContrasenaRequest request) {

        restablecerContrasenaUseCase.ejecutar(request.email(), request.codigo(), request.nuevaContrasena());
        return ResponseEntity.noContent().build(); // 204
    }
}
