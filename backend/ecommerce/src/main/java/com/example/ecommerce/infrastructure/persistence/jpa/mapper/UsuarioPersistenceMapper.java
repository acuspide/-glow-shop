package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.FechaNacimiento;
import com.example.ecommerce.domain.valueobject.Telefono;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.UsuarioEntity;
import org.springframework.stereotype.Component;

@Component
public class UsuarioPersistenceMapper {

    public UsuarioEntity toEntity(Usuario usuario) {
        UsuarioEntity entity = new UsuarioEntity();
        entity.setId(usuario.getId());
        entity.setNombre(usuario.getNombre());
        entity.setEmail(usuario.getEmail().getValor());
        entity.setContrasenaHash(usuario.getContrasenaHash());
        entity.setTelefono(usuario.getTelefono().valor());
        entity.setFechaNacimiento(usuario.getFechaNacimiento().valor());
        entity.setRol(usuario.getRol());
        entity.setActivo(usuario.isActivo());
        return entity;
    }

    public Usuario toDomain(UsuarioEntity entity) {
        return Usuario.reconstruir(
                entity.getId(),
                entity.getNombre(),
                new Email(entity.getEmail()),
                entity.getContrasenaHash(),
                new Telefono(entity.getTelefono()),
                new FechaNacimiento(entity.getFechaNacimiento()),
                entity.getRol(),
                entity.isActivo());
    }
}
