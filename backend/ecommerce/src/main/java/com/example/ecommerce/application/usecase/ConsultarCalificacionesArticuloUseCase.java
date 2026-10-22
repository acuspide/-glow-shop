package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.valueobject.ResumenCalificaciones;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-91 Consultar calificaciones de un artículo: cualquiera ve el promedio y los comentarios.
 */
@Service
@RequiredArgsConstructor
public class ConsultarCalificacionesArticuloUseCase {

    private final CalificacionRepository calificacionRepository;
    private final ArticuloRepository articuloRepository;

    @Transactional(readOnly = true)
    public ResumenCalificaciones ejecutar(String articuloId) {
        articuloRepository.obtenerPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId)); // -> 404

        return new ResumenCalificaciones(articuloId, calificacionRepository.listarPorArticulo(articuloId));
    }
}
