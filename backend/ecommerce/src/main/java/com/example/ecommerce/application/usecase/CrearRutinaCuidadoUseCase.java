package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
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

        List<Articulo> articulos = new ArrayList<>();

        for (String articuloId : articulosIds) {
            Articulo articulo = articuloRepository
                    .obtenerPorId(articuloId)
                    .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));

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