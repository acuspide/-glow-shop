package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AgregarArticuloARutinaUseCase {
    private final RutinaCuidadoRepository rutinaRepository;
    private final ArticuloRepository articuloRepository;

    @Transactional
    public RutinaCuidado ejecutar(String rutinaId, String articuloId) {

        RutinaCuidado rutina = rutinaRepository
                .obtenerPorId(rutinaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la rutina", rutinaId));

        Articulo articulo = articuloRepository
                .obtenerPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

        RutinaCuidado rutinaActualizada = rutina.agregarArticulo(articulo);

        rutinaRepository.guardar(rutinaActualizada);

        return rutinaActualizada;
    }
}
