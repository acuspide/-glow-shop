package com.example.ecommerce.infrastructure.persistence.jpa.mapper;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.ArticuloEntity;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.CategoriaEntity;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.MarcaEntity;
import com.example.ecommerce.infrastructure.persistence.jpa.entity.TiendaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ArticuloPersistenceMapper {

    private final CategoriaPersistenceMapper categoriaMapper;
    private final MarcaPersistenceMapper marcaMapper;
    private final TiendaPersistenceMapper tiendaMapper;

    /** Categoría, marca y tienda llegan como referencias a filas que ya existen en la base de datos. */
    public ArticuloEntity toEntity(Articulo articulo, CategoriaEntity categoria, MarcaEntity marca, TiendaEntity tienda) {
        ArticuloEntity entity = new ArticuloEntity();
        entity.setId(articulo.getId());
        entity.setNombre(articulo.getNombre().valor());
        entity.setPrecio(articulo.getPrecio().valor());
        entity.setCategoria(categoria);
        entity.setMarca(marca);
        entity.setTienda(tienda);
        entity.setTono(articulo.getTono());
        entity.setTiposPiel(new ArrayList<>(articulo.getTiposPiel()));
        entity.setCantidadDisponible(articulo.getCantidadDisponible());
        entity.setFechaVencimiento(articulo.getFechaVencimiento().fecha());
        entity.setEliminado(articulo.isEliminado());
        entity.setPublicado(articulo.isPublicado());
        return entity;
    }

    public Articulo toDomain(ArticuloEntity entity) {
        return Articulo.reconstruir(
                entity.getId(),
                new NombreArticulo(entity.getNombre()),
                new Precio(entity.getPrecio()),
                categoriaMapper.toDomain(entity.getCategoria()),
                marcaMapper.toDomain(entity.getMarca()),
                entity.getTono(),
                List.copyOf(entity.getTiposPiel()),
                Inventario.crear(entity.getCantidadDisponible()),
                new FechaVencimiento(entity.getFechaVencimiento()),
                tiendaMapper.toDomain(entity.getTienda()),
                entity.isEliminado(),
                entity.isPublicado());
    }
}
