package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.BuscarArticulosRequest;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;
import com.example.ecommerce.application.dto.response.PageResponse;
import com.example.ecommerce.application.usecase.BuscarArticulosUseCase;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import com.example.ecommerce.infrastructure.rest.mapper.BuscarArticulosMapper;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buscarArticulos")
public class BuscarArticulosController {
    private final BuscarArticulosUseCase buscarArticulosUseCase;
    private final BuscarArticulosMapper buscarArticulosMapper;
    private final MostrarDetalleArticuloMapper mostrarDetalleArticuloMapper;

    @GetMapping
    public ResponseEntity<PageResponse<MostrarDetalleArticuloResponse>> buscar(
            @ModelAttribute BuscarArticulosRequest request,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        CriteriosBusquedaArticulo criterios =
                buscarArticulosMapper.toCriterios(request);

        PageResponse<MostrarDetalleArticuloResponse> respuesta =
                PageResponse.desde(
                        buscarArticulosUseCase.ejecutar(criterios, page, size),
                        mostrarDetalleArticuloMapper::toResponse);

        return ResponseEntity.ok(respuesta);
    }
}
