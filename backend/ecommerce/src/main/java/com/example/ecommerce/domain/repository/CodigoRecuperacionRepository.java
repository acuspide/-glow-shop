package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.CodigoRecuperacion;

import java.util.Optional;

public interface CodigoRecuperacionRepository {

    /** Guarda el código; si el usuario ya tenía uno, lo reemplaza (solo vale el último). */
    void guardar(CodigoRecuperacion codigo);

    Optional<CodigoRecuperacion> obtenerPorUsuarioId(String usuarioId);
}
