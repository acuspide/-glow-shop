package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.CalificarArticuloRequest;
import com.example.ecommerce.application.dto.response.CalificacionResponse;
import com.example.ecommerce.application.dto.response.CalificacionesArticuloResponse;
import com.example.ecommerce.application.usecase.ActualizarCalificacionUseCase;
import com.example.ecommerce.application.usecase.CalificarArticuloUseCase;
import com.example.ecommerce.application.usecase.EliminarCalificacionUseCase;
import com.example.ecommerce.application.usecase.ConsultarCalificacionesArticuloUseCase;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.infrastructure.rest.mapper.CalificacionMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
@RequestMapping("/api")
public class CalificacionController {

    private final CalificarArticuloUseCase calificarArticuloUseCase;
    private final ConsultarCalificacionesArticuloUseCase consultarCalificacionesUseCase;
    private final ActualizarCalificacionUseCase actualizarCalificacionUseCase;
    private final EliminarCalificacionUseCase eliminarCalificacionUseCase;
    private final TokenService tokenService;
    private final CalificacionMapper mapper;

    // CU-91: público. Promedio y comentarios de un artículo, del más reciente al más antiguo.
    @GetMapping("/articulos/{articuloId}/calificaciones")
    public ResponseEntity<CalificacionesArticuloResponse> consultar(@PathVariable String articuloId) {
        return ResponseEntity.ok(mapper.toResumenResponse(consultarCalificacionesUseCase.ejecutar(articuloId)));
    }

    // CU-90: el cliente con sesión iniciada califica un artículo que recibió.
    @PostMapping("/articulos/{articuloId}/calificaciones")
    public ResponseEntity<CalificacionResponse> calificar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String articuloId,
            @Valid @RequestBody CalificarArticuloRequest request) {

        String clienteId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Calificacion calificacion = calificarArticuloUseCase.ejecutar(
                UUID.randomUUID().toString(), clienteId, articuloId, request.puntuacion(), request.comentario());

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/calificaciones/{id}")
                .buildAndExpand(calificacion.getId())
                .toUri();

        return ResponseEntity.created(location).body(mapper.toResponse(calificacion));
    }

    // CU-92: el autor edita su calificación (mismas reglas que al crearla: 1-5 estrellas, comentario hasta 500).
    @PutMapping("/calificaciones/{id}")
    public ResponseEntity<CalificacionResponse> actualizar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @Valid @RequestBody CalificarArticuloRequest request) {

        String clienteId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Calificacion calificacion = actualizarCalificacionUseCase.ejecutar(
                id, clienteId, request.puntuacion(), request.comentario());

        return ResponseEntity.ok(mapper.toResponse(calificacion));
    }

    // CU-93: el autor retira su calificación (baja lógica).
    @DeleteMapping("/calificaciones/{id}")
    public ResponseEntity<Void> eliminar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id) {

        String clienteId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        eliminarCalificacionUseCase.ejecutar(id, clienteId);

        return ResponseEntity.noContent().build(); // 204
    }
}
