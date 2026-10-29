package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.application.dto.request.PublicarArticuloRequest;
import com.example.ecommerce.application.dto.response.PublicarArticuloResponse;
import com.example.ecommerce.application.usecase.PublicarArticuloUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.application.dto.response.ListarArticulosResponse;
import com.example.ecommerce.application.usecase.ListarArticulosUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ListarArticulosMapper;
import lombok.RequiredArgsConstructor;

import com.example.ecommerce.application.dto.response.ConsultarInventarioResponse;
import com.example.ecommerce.application.usecase.ConsultarInventarioUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioMapper;

import java.util.List;
import java.util.UUID;
import com.example.ecommerce.infrastructure.rest.mapper.PublicarArticuloMapper;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;
import com.example.ecommerce.application.usecase.MostrarDetalleArticuloUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/articulos")
public class PublicarArticuloController {
    private final PublicarArticuloUseCase publicarArticuloUseCase;
    private final PublicarArticuloMapper mapper;
    private final MostrarDetalleArticuloUseCase mostrarDetalleArticuloUseCase;
    private final MostrarDetalleArticuloMapper mostrarDetalleArticuloMapper;
    private final ListarArticulosUseCase listarArticulosUseCase;
    private final ListarArticulosMapper listarArticulosMapper;
    private final ConsultarInventarioUseCase consultarInventarioUseCase;
    private final ConsultarInventarioMapper consultarInventarioMapper;
    private final TokenService tokenService;

    @GetMapping
    public ResponseEntity<List<ListarArticulosResponse>> listarArticulos() {

        List<Articulo> articulos = listarArticulosUseCase.ejecutar();

        List<ListarArticulosResponse> response =
                listarArticulosMapper.toResponseList(articulos);

        return ResponseEntity.ok(response);
    }
    // Publicar artículo: el vendedor sale del token y publica en su tienda activa.
    @PostMapping
    public ResponseEntity<PublicarArticuloResponse> publicar(
            @RequestHeader(value = "Authorization", required = false) String autorizacion,
            @Valid @RequestBody PublicarArticuloRequest request) {

        String vendedorId = tokenService.extraerUsuarioId(BearerToken.extraer(autorizacion));

        Articulo articuloPublicado = publicarArticuloUseCase.ejecutar(
                UUID.randomUUID().toString(),
                vendedorId,
                new NombreArticulo(request.nombre()),
                new Precio(request.precio()),
                request.categoriaId(),
                request.marcaId(),
                request.tono(),
                request.tiposPiel(),
                Inventario.crear(request.cantidadDisponible()),
                new FechaVencimiento(request.fechaVencimiento()),
                request.tiendaId()
        );

        PublicarArticuloResponse response =
                mapper.toResponse(articuloPublicado);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(articuloPublicado.getId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }
    @GetMapping("/{id}")
    public ResponseEntity<MostrarDetalleArticuloResponse> mostrarDetalle(
            @PathVariable String id) {

        Articulo articulo = mostrarDetalleArticuloUseCase.ejecutar(id);

        MostrarDetalleArticuloResponse response =
                mostrarDetalleArticuloMapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/inventario")
    public ResponseEntity<ConsultarInventarioResponse> consultarInventario(
            @PathVariable String id) {

        Articulo articulo = consultarInventarioUseCase.ejecutar(id);

        ConsultarInventarioResponse response =
                consultarInventarioMapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

}
