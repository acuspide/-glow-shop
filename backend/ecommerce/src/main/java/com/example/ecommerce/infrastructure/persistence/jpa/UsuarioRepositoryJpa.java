package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.UsuarioPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UsuarioRepositoryJpa implements UsuarioRepository {

    private final UsuarioJpaDataRepository dataRepository;
    private final UsuarioPersistenceMapper mapper;

    @Override
    public Optional<Usuario> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Usuario> listarTodos() {
        return dataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Usuario> buscarPorEmail(Email email) {
        return dataRepository.findByEmail(email.getValor()).map(mapper::toDomain);
    }

    @Override
    public boolean existePorEmail(Email email) {
        return dataRepository.existsByEmail(email.getValor());
    }

    @Override
    public void guardar(Usuario usuario) {
        dataRepository.save(mapper.toEntity(usuario));
    }
}
