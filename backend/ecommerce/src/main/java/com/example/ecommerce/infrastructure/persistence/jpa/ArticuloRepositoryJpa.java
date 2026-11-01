package com.example.ecommerce.infrastructure.persistence.jpa;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.Pagina;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.ArticuloEntity;
import com.example.ecommerce.infrastructure.persistence.jpa.mapper.ArticuloPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ArticuloRepositoryJpa implements ArticuloRepository {

    private final ArticuloJpaDataRepository dataRepository;
    private final CategoriaJpaDataRepository categoriaDataRepository;
    private final MarcaJpaDataRepository marcaDataRepository;
    private final TiendaJpaDataRepository tiendaDataRepository;
    private final ArticuloPersistenceMapper mapper;

    @Override
    public Optional<Articulo> obtenerPorId(String id) {
        return dataRepository.findById(id).map(mapper::toDomain);
    }

    // La categoría, la marca y la tienda ya están guardadas: se enlazan por id (llave foránea).
    @Override
    public void guardar(Articulo articulo) {
        dataRepository.save(mapper.toEntity(
                articulo,
                categoriaDataRepository.getReferenceById(articulo.getCategoria().getId()),
                marcaDataRepository.getReferenceById(articulo.getMarca().getId()),
                tiendaDataRepository.getReferenceById(articulo.getTienda().getId())));
    }

    @Override
    public List<Articulo> listarTodos() {
        return dataRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    // Aquí (y solo aquí) se usan Page y PageRequest de Spring: se convierten en la Pagina del dominio.
    @Override
    public Pagina<Articulo> buscar(CriteriosBusquedaArticulo criterios, int pagina, int tamano) {
        String texto = (criterios.texto() == null) ? "" : criterios.texto().trim();

        Page<ArticuloEntity> resultado = dataRepository.buscar(
                texto,
                criterios.categoriaId(),
                criterios.marcaId(),
                criterios.tiendaId(),
                criterios.precioMinimo(),
                criterios.precioMaximo(),
                criterios.tono(),
                criterios.tipoPiel(),
                PageRequest.of(pagina, tamano, Sort.by("nombre").and(Sort.by("id"))));

        return new Pagina<>(
                resultado.getContent().stream().map(mapper::toDomain).toList(),
                resultado.getTotalElements(),
                resultado.getTotalPages(),
                resultado.getNumber(),
                resultado.getSize());
    }
}
