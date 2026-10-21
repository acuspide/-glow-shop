package com.example.ecommerce.domain.valueobject;

import com.example.ecommerce.domain.entity.Usuario;

/**
 * Criterios opcionales para listar usuarios. Un criterio en null significa "no filtrar por eso".
 */
public record FiltroUsuarios(RolUsuario rol, Boolean activo) {

    public boolean admite(Usuario usuario) {
        return (rol == null || usuario.getRol() == rol)
                && (activo == null || usuario.isActivo() == activo);
    }
}
