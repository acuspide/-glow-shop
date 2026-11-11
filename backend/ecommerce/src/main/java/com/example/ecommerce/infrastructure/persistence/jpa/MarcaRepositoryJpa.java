package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.MarcaPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Sort;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MarcaRepositoryJpa implements MarcaRepository {

    private final MarcaJpaDataRepository dataRepository;
    private final MarcaPersistenceMapper mapper;

    @Override
    public void guardar(Marca marca) {
        dataRepository.save(mapper.toEntity(marca));
    }

    @Override
    public Optional<Marca> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    // El nombre es único sin distinguir mayúsculas ("Labios" y "labios" son la misma).
    @Override
    public boolean existePorNombre(String nombre) {
        return dataRepository.existsByNombreIgnoreCase(nombre.trim());
    }

    @Override
    public List<Marca> listarTodas() {
        return dataRepository.findAll(Sort.by("nombre")).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
