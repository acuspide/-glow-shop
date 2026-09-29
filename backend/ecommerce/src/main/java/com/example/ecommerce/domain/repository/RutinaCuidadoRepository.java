package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import java.util.Optional;

public interface RutinaCuidadoRepository {
    Optional<RutinaCuidado> obtenerPorId(long id);

    void guardar(RutinaCuidado rutina);
}
