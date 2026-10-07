package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.dto.response.UsuarioResponse;
import com.example.ecommerce.domain.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail().getValor(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }
}
