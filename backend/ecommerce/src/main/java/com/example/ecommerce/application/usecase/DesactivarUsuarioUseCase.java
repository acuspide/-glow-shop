package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-08 Desactivar usuario: el administrador bloquea una cuenta sin borrar su historial.
 */
@Service
@RequiredArgsConstructor
public class DesactivarUsuarioUseCase {

    private final UsuarioRepository repository;

    @Transactional
    public void ejecutar(String administradorId, String usuarioId) {
        Usuario administrador = repository.obtenerPorId(administradorId)
                .orElseThrow(AccionSoloParaAdministradorException::new);
        Usuario usuario = repository.obtenerPorId(usuarioId).orElseThrow(() -> new RecursoNoEncontradoException("el usuario", usuarioId));

        usuario.desactivarPor(administrador);
        repository.guardar(usuario);
    }
}
