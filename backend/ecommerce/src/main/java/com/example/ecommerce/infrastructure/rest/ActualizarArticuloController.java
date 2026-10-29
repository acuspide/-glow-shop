package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.request.ActualizarArticuloRequest;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;
import com.example.ecommerce.application.usecase.ActualizarArticuloUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articulos")
public class ActualizarArticuloController {
    private final ActualizarArticuloUseCase actualizarArticuloUseCase;
    private final MostrarDetalleArticuloMapper mapper;
    private final TokenService tokenService;

    // Actualizar artículo: el vendedor sale del token; el caso de uso verifica que sea el dueño de la tienda.
    @PutMapping("/{id}")
    public ResponseEntity<MostrarDetalleArticuloResponse> actualizar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @PathVariable String id,
            @Valid @RequestBody ActualizarArticuloRequest request) {

        String vendedorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));

        Articulo articulo = actualizarArticuloUseCase.ejecutar(
                id,
                vendedorId,
                new NombreArticulo(request.nombre()),
                new Precio(request.precio()),
                request.categoriaId(),
                request.marcaId(),
                request.tono(),
                request.tiposPiel(),
                new FechaVencimiento(request.fechaVencimiento())
        );

        return ResponseEntity.ok(mapper.toResponse(articulo));
    }
}
