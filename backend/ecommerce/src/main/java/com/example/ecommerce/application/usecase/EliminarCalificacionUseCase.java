package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-93 Eliminar calificación: el cliente retira su propia calificación (baja lógica).
 */
@Service
@RequiredArgsConstructor
public class EliminarCalificacionUseCase {

    private final CalificacionRepository calificacionRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public void ejecutar(String calificacionId, String clienteId) {
        Usuario cliente = usuarioRepository.obtenerPorId(clienteId)
                .orElseThrow(AccionSoloParaClienteException::new);
        Calificacion calificacion = calificacionRepository.obtenerPorId(calificacionId).orElseThrow(() -> new RecursoNoEncontradoException("la calificación", calificacionId));

        calificacion.eliminarPor(cliente);
        calificacionRepository.guardar(calificacion);
    }
}
