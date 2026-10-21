package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.ActualizarPerfilRequest;
import com.example.ecommerce.application.dto.request.CambiarContrasenaRequest;
import com.example.ecommerce.application.dto.request.RegistrarClienteRequest;
import com.example.ecommerce.application.dto.request.RegistrarVendedorRequest;
import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.application.usecase.ActualizarPerfilUseCase;
import com.example.ecommerce.application.usecase.CambiarContrasenaUseCase;
import com.example.ecommerce.application.usecase.ConsultarPerfilUseCase;
import com.example.ecommerce.application.usecase.RegistrarClienteUseCase;
import com.example.ecommerce.application.usecase.RegistrarVendedorUseCase;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.infrastructure.rest.mapper.UsuarioMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final RegistrarClienteUseCase registrarClienteUseCase;
    private final RegistrarVendedorUseCase registrarVendedorUseCase;
    private final ConsultarPerfilUseCase consultarPerfilUseCase;
    private final ActualizarPerfilUseCase actualizarPerfilUseCase;
    private final CambiarContrasenaUseCase cambiarContrasenaUseCase;
    private final TokenService tokenService;
    private final UsuarioMapper mapper;

    // CU-06: edita nombre y teléfono del usuario con sesión iniciada.
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponse> actualizarPerfil(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody ActualizarPerfilRequest request) {

        String usuarioId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Usuario usuario = actualizarPerfilUseCase.ejecutar(usuarioId, request.nombre(), request.telefono());

        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    // CU-06: cambia la contraseña conociendo la actual.
    @PutMapping("/me/contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody CambiarContrasenaRequest request) {

        String usuarioId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        cambiarContrasenaUseCase.ejecutar(usuarioId, request.contrasenaActual(), request.nuevaContrasena());

        return ResponseEntity.noContent().build(); // 204
    }

    // CU-05: perfil del usuario con sesión iniciada. Requiere "Authorization: Bearer <token>".
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponse> consultarPerfil(
            @RequestHeader(value = "Authorization", required = false) String autorizacion) {

        String usuarioId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Usuario usuario = consultarPerfilUseCase.ejecutar(usuarioId);

        return ResponseEntity.ok(mapper.toResponse(usuario));
    }

    // Autorregistro público: siempre crea un CLIENTE.
    @PostMapping
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody RegistrarClienteRequest request) {

        Usuario cliente = registrarClienteUseCase.ejecutar(
                UUID.randomUUID().toString(),
                request.nombre(),
                request.email(),
                request.contrasena(),
                request.telefono(),
                request.fechaNacimiento()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(cliente));
    }

    // Autorregistro público de vendedor: siempre crea un VENDEDOR.
    @PostMapping("/vendedores")
    public ResponseEntity<UsuarioResponse> registrarVendedor(
            @Valid @RequestBody RegistrarVendedorRequest request) {

        Usuario vendedor = registrarVendedorUseCase.ejecutar(
                UUID.randomUUID().toString(),
                request.nombre(),
                request.email(),
                request.contrasena(),
                request.telefono(),
                request.fechaNacimiento()
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/usuarios/{id}")
                .buildAndExpand(vendedor.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(vendedor));
    }
}
