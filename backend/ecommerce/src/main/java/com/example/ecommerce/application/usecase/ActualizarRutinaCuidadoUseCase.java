package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
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
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public RutinaCuidado ejecutar(
            String id,
            String administradorId,
            String nombre,
            List<String> articulosIds) {

        Usuario administrador = usuarioRepository.obtenerPorId(administradorId)
                .orElseThrow(AccionSoloParaAdministradorException::new);
        administrador.verificarQueEsAdministradorActivo();   // solo el administrador arma las rutinas

        rutinaRepository.obtenerPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("la rutina", id));

        List<Articulo> articulos = new ArrayList<>();

        for (String articuloId : articulosIds) {
            Articulo articulo = articuloRepository
                    .obtenerPorId(articuloId)
                    .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

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
