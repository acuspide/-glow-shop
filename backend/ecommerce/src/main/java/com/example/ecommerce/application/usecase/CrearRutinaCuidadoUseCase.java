package com.example.ecommerce.application.usecase;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;

import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class CrearRutinaCuidadoUseCase {
    private final RutinaCuidadoRepository rutinaRepository;
    private final ArticuloRepository articuloRepository;

    @Transactional
    public RutinaCuidado ejecutar(
            long id,
            String nombre,
            List<Long> articulosIds) {

        List<Articulo> articulos = new ArrayList<>();

        for (Long articuloId : articulosIds) {
            Articulo articulo = articuloRepository
                    .obtenerPorId(articuloId)
                    .orElseThrow();

            articulos.add(articulo);
        }

        RutinaCuidado rutina = RutinaCuidado.crear(
                id,
                nombre,
                articulos
        );

        rutinaRepository.guardar(rutina);

        return rutina;
    }
}