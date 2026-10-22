package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActualizarRutinaCuidadoUseCase {
    private final RutinaCuidadoRepository rutinaRepository;
    private final ArticuloRepository articuloRepository;

    @Transactional
    public RutinaCuidado ejecutar(
            long id,
            String nombre,
            List<Long> articulosIds) {

        rutinaRepository.obtenerPorId(id)
                .orElseThrow();

        List<Articulo> articulos = new ArrayList<>();

        for (Long articuloId : articulosIds) {
            Articulo articulo = articuloRepository
                    .obtenerPorId(articuloId)
                    .orElseThrow();

            articulos.add(articulo);
        }

        RutinaCuidado rutinaActualizada = RutinaCuidado.crear(
                id,
                nombre,
                articulos
        );

        rutinaRepository.guardar(rutinaActualizada);

        return rutinaActualizada;
    }
}
