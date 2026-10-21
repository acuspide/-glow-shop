package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCalificacion;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * CU-90 Calificar artículo comprado: el cliente puntúa (1 a 5) y comenta un artículo que recibió.
 */
@Service
@RequiredArgsConstructor
public class CalificarArticuloUseCase {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ArticuloRepository articuloRepository;
    private final VerificadorDeCalificacion verificadorDeCalificacion;

    @Transactional
    public Calificacion ejecutar(String id, String clienteId, long articuloId, int estrellas, String comentario) {
        Usuario cliente = usuarioRepository.obtenerPorId(clienteId)
                .orElseThrow(AccionSoloParaClienteException::new);
        articuloRepository.obtenerPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId)); // -> 404

        verificadorDeCalificacion.verificarQuePuedeCalificar(clienteId, articuloId);
        Calificacion calificacion = Calificacion.calificar(
                id, articuloId, cliente, new Puntuacion(estrellas), comentario, LocalDateTime.now());

        calificacionRepository.guardar(calificacion);
        return calificacion;
    }
}
