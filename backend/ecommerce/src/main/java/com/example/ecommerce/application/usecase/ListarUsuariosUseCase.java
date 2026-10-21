package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.FiltroUsuarios;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * CU-07 Listar usuarios: el administrador ve los usuarios registrados, con filtros opcionales
 * por rol y por estado (activo o desactivado), ordenados por nombre.
 */
@Service
@RequiredArgsConstructor
public class ListarUsuariosUseCase {

    private final UsuarioRepository repository;

    @Transactional(readOnly = true)
    public List<Usuario> ejecutar(String administradorId, FiltroUsuarios filtro) {
        Usuario administrador = repository.obtenerPorId(administradorId)
                .orElseThrow(AccionSoloParaAdministradorException::new);
        administrador.verificarQueEsAdministradorActivo();

        return repository.listarTodos().stream()
                .filter(filtro::admite)
                .sorted(Comparator.comparing(Usuario::getNombre, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
