package com.example.ecommerce.infrastructure.rest.mapper;

import com.example.ecommerce.application.SesionIniciada;
import com.example.ecommerce.application.dto.response.LoginResponse;
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
                usuario.getTelefono().valor(),
                usuario.getFechaNacimiento().valor(),
                usuario.getRol(),
                usuario.isActivo()
        );
    }

    public LoginResponse toLoginResponse(SesionIniciada sesion) {
        return new LoginResponse(sesion.token(), "Bearer", toResponse(sesion.usuario()));
    }
}
