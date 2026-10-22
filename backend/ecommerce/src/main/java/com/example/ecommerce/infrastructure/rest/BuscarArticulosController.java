package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.BuscarArticulosRequest;
import com.example.ecommerce.application.dto.response.MostrarDetalleArticuloResponse;
import com.example.ecommerce.application.usecase.BuscarArticulosUseCase;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import com.example.ecommerce.infrastructure.rest.mapper.BuscarArticulosMapper;
import com.example.ecommerce.infrastructure.rest.mapper.MostrarDetalleArticuloMapper;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/buscarArticulos")
public class BuscarArticulosController {
    private final BuscarArticulosUseCase buscarArticulosUseCase;
    private final BuscarArticulosMapper buscarArticulosMapper;
    private final MostrarDetalleArticuloMapper mostrarDetalleArticuloMapper;

    @GetMapping
    public ResponseEntity<List<MostrarDetalleArticuloResponse>> buscar(
            @ModelAttribute BuscarArticulosRequest request) {

        CriteriosBusquedaArticulo criterios =
                buscarArticulosMapper.toCriterios(request);

        List<MostrarDetalleArticuloResponse> respuesta =
                buscarArticulosUseCase.ejecutar(criterios)
                        .stream()
                        .map(mostrarDetalleArticuloMapper::toResponse)
                        .toList();

        return ResponseEntity.ok(respuesta);
    }
}
