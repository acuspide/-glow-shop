package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CU-05 Consultar perfil: devuelve los datos de la cuenta del usuario que tiene la sesión iniciada.
 */
@Service
@RequiredArgsConstructor
public class ConsultarPerfilUseCase {

    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public Usuario ejecutar(String usuarioId) {
        return repository.obtenerPorId(usuarioId).orElseThrow(); // no existe -> 404
    }
}
