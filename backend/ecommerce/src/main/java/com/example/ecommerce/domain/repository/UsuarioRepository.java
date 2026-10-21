package com.example.ecommerce.domain.repository;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.valueobject.Email;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> obtenerPorId(String id);
    List<Usuario> listarTodos();
    Optional<Usuario> buscarPorEmail(Email email);
    boolean existePorEmail(Email email);
    void guardar(Usuario usuario);
}