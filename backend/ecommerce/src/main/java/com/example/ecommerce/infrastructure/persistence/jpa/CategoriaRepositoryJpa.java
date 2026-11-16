package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.CategoriaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CategoriaRepositoryJpa implements CategoriaRepository {

    private final CategoriaJpaDataRepository dataRepository;
    private final CategoriaPersistenceMapper mapper;

    @Override
    public void guardar(Categoria categoria) {
        dataRepository.save(mapper.toEntity(categoria));
    }

    @Override
    public Optional<Categoria> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    // El nombre es único sin distinguir mayúsculas ("Labios" y "labios" son la misma).
    @Override
    public boolean existePorNombre(String nombre) {
        return dataRepository.existsByNombreIgnoreCase(nombre.trim());
    }

    @Override
    public List<Categoria> listarTodas() {
        return dataRepository.findAll(Sort.by("nombre")).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
