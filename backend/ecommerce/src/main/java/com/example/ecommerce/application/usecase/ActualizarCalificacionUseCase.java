package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-92 Actualizar calificación: el cliente edita su propia calificación.
 */
@Service
@RequiredArgsConstructor
public class ActualizarCalificacionUseCase {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public Calificacion ejecutar(String calificacionId, String clienteId, int estrellas, String comentario) {
        Usuario cliente = usuarioRepository.obtenerPorId(clienteId)
                .orElseThrow(AccionSoloParaClienteException::new);
        Calificacion calificacion = calificacionRepository.obtenerPorId(calificacionId).orElseThrow(() -> new RecursoNoEncontradoException("la calificación", calificacionId));

        calificacion.actualizarPor(cliente, new Puntuacion(estrellas), comentario);
        calificacionRepository.guardar(calificacion);

        return calificacion;
    }
}
