package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.springframework.stereotype.Service;

@Service
public class AgregarArticuloARutinaUseCase {
    private final RutinaCuidadoRepository rutinaRepository;
    private final ArticuloRepository articuloRepository;

    public AgregarArticuloARutinaUseCase(
            RutinaCuidadoRepository rutinaRepository,
            ArticuloRepository articuloRepository) {
        this.rutinaRepository = rutinaRepository;
        this.articuloRepository = articuloRepository;
    }

    public RutinaCuidado ejecutar(long rutinaId, long articuloId) {

        RutinaCuidado rutina = rutinaRepository
                .obtenerPorId(rutinaId)
                .orElseThrow();

        Articulo articulo = articuloRepository
                .obtenerPorId(articuloId)
                .orElseThrow();

        RutinaCuidado rutinaActualizada = rutina.agregarArticulo(articulo);

        rutinaRepository.guardar(rutinaActualizada);

        return rutinaActualizada;
    }
}
