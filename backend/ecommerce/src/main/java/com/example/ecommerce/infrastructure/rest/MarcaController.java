package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.CrearMarcaRequest;
import com.example.ecommerce.application.dto.response.CrearMarcaResponse;
import com.example.ecommerce.application.usecase.CrearMarcaUseCase;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.infrastructure.rest.mapper.MarcaMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import com.example.ecommerce.application.dto.response.MarcaResponse;
import com.example.ecommerce.application.usecase.ListarMarcasUseCase;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;
import java.net.URI;
import java.util.UUID;

// Solo el administrador crea; el administrador sale del token y el caso de uso verifica su rol.
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/marcas")
public class MarcaController {
    private final CrearMarcaUseCase crearMarcaUseCase;
    private final ListarMarcasUseCase listarMarcasUseCase;
    private final MarcaMapper mapper;
    private final TokenService tokenService;

    // Público: lo usan el formulario de publicar artículo y los filtros del catálogo.
    @GetMapping
    public ResponseEntity<List<MarcaResponse>> listar() {
        List<MarcaResponse> marcas = listarMarcasUseCase.ejecutar()
                .stream()
                .map(mapper::toMarcaResponse)
                .toList();

        return ResponseEntity.ok(marcas);
    }
    @PostMapping
    public ResponseEntity<CrearMarcaResponse> crear(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody CrearMarcaRequest request) {

        String administradorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));
        Marca marca = crearMarcaUseCase.ejecutar(
                UUID.randomUUID().toString(),
                administradorId,
                request.nombre()
        );

        CrearMarcaResponse response = mapper.toResponse(marca);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(marca.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
}
