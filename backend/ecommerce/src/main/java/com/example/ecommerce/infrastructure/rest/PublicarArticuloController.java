package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.PublicarArticuloRequest;
import com.example.ecommerce.application.dto.response.PublicarArticuloResponse;
import com.example.ecommerce.application.usecase.PublicarArticuloUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.application.dto.response.ListarArticulosResponse;
import com.example.ecommerce.application.usecase.ListarArticulosUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ListarArticulosMapper;

import com.example.ecommerce.application.dto.response.ConsultarInventarioResponse;
import com.example.ecommerce.application.usecase.ConsultarInventarioUseCase;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioMapper;

import java.util.List;
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

    public PublicarArticuloController(
            PublicarArticuloUseCase publicarArticuloUseCase,
            PublicarArticuloMapper mapper,
            MostrarDetalleArticuloUseCase mostrarDetalleArticuloUseCase,
            MostrarDetalleArticuloMapper mostrarDetalleArticuloMapper,
            ListarArticulosUseCase listarArticulosUseCase,
            ListarArticulosMapper listarArticulosMapper,
            ConsultarInventarioUseCase consultarInventarioUseCase,
            ConsultarInventarioMapper consultarInventarioMapper) {

        this.publicarArticuloUseCase = publicarArticuloUseCase;
        this.mapper = mapper;
        this.mostrarDetalleArticuloUseCase = mostrarDetalleArticuloUseCase;
        this.mostrarDetalleArticuloMapper = mostrarDetalleArticuloMapper;
        this.listarArticulosUseCase = listarArticulosUseCase;
        this.listarArticulosMapper = listarArticulosMapper;
        this.consultarInventarioUseCase = consultarInventarioUseCase;
        this.consultarInventarioMapper = consultarInventarioMapper;
    }
    @GetMapping
    public ResponseEntity<List<ListarArticulosResponse>> listarArticulos() {

        List<Articulo> articulos = listarArticulosUseCase.ejecutar();

        List<ListarArticulosResponse> response =
                listarArticulosMapper.toResponseList(articulos);

        return ResponseEntity.ok(response);
    }
    @PostMapping
    public ResponseEntity<PublicarArticuloResponse> publicar(
            @Valid @RequestBody PublicarArticuloRequest request) {

        Articulo articulo = mapper.toEntity(request);

        Articulo articuloPublicado =
                publicarArticuloUseCase.ejecutar(articulo);

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
            @PathVariable long id) {

        Articulo articulo = mostrarDetalleArticuloUseCase.ejecutar(id);

        MostrarDetalleArticuloResponse response =
                mostrarDetalleArticuloMapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/inventario")
    public ResponseEntity<ConsultarInventarioResponse> consultarInventario(
            @PathVariable long id) {

        Articulo articulo = consultarInventarioUseCase.ejecutar(id);

        ConsultarInventarioResponse response =
                consultarInventarioMapper.toResponse(articulo);

        return ResponseEntity.ok(response);
    }

}
