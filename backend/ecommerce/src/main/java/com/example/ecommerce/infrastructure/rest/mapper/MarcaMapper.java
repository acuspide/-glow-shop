package com.example.ecommerce.infrastructure.rest.mapper;
import com.example.ecommerce.application.dto.response.CrearMarcaResponse;
import com.example.ecommerce.domain.entity.Marca;
import org.springframework.stereotype.Component;

@Component
public class MarcaMapper {
    public CrearMarcaResponse toResponse(Marca marca) {
        return new CrearMarcaResponse(
                marca.getId(),
                marca.getNombre()
        );
    }
}
