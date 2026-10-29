package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Telefono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-06 Actualizar perfil (datos personales): el usuario con sesión iniciada edita su nombre y teléfono.
 */
@Service
@RequiredArgsConstructor
public class ActualizarPerfilUseCase {

    private final UsuarioRepository repository;

    @Transactional
    public Usuario ejecutar(String usuarioId, String nombre, String telefono) {
        Usuario usuario = repository.obtenerPorId(usuarioId).orElseThrow(() -> new RecursoNoEncontradoException("el usuario", usuarioId));

        usuario.actualizarDatos(nombre, new Telefono(telefono));
        repository.guardar(usuario);

        return usuario;
    }
}
