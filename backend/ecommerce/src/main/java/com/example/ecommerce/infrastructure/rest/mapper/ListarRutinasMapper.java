package com.example.ecommerce.infrastructure.rest.mapper;
import com.example.ecommerce.application.dto.response.ListarRutinasResponse;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ListarRutinasMapper {
    public ListarRutinasResponse toResponse(
            List<RutinaCuidado> rutinas) {

        List<ListarRutinasResponse.RutinaResponse> rutinasResponse =
                rutinas.stream()
                        .map(rutina -> {
                            List<ListarRutinasResponse.ArticuloResponse>
                                    articulosResponse =
                                    rutina.getArticulos().stream()
                                            .map(articulo ->
                                                    new ListarRutinasResponse.ArticuloResponse(
                                                            articulo.getId(),
                                                            articulo.getNombre().valor(),
                                                            articulo.getCantidadDisponible()
                                                    ))
                                            .toList();

                            return new ListarRutinasResponse.RutinaResponse(
                                    rutina.getId(),
                                    rutina.getNombre(),
                                    articulosResponse
                            );
                        })
                        .toList();

        return new ListarRutinasResponse(rutinasResponse);
    }
}
