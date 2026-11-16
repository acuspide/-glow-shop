package com.example.ecommerce.infrastructure.rest.mapper;
import com.example.ecommerce.application.dto.response.CrearCategoriaResponse;
import com.example.ecommerce.domain.entity.Categoria;
import org.springframework.stereotype.Component;
import com.example.ecommerce.application.dto.response.CategoriaResponse;
@Component
public class CategoriaMapper {
    public CrearCategoriaResponse toResponse(Categoria categoria) {
        return new CrearCategoriaResponse(
                categoria.getId(),
                categoria.getNombre()
        );
    }

    public CategoriaResponse toCategoriaResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNombre()
        );
    }
}
