package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.*;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.*;
import org.springframework.stereotype.Service;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PublicarArticuloUseCase {

    private final ArticuloRepository repository;

    @Transactional
    public Articulo ejecutar(
            String id,
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            Inventario inventario,
            FechaVencimiento fechaVencimiento,
            Tienda tienda) {

        Articulo articulo = Articulo.crear(
                id,
                nombre,
                precio,
                categoria,
                marca,
                tono,
                tiposPiel,
                inventario,
                fechaVencimiento,
                tienda
        );

        articulo.publicar();

        repository.guardar(articulo);

        return articulo;
    }
    @Transactional
    public Articulo ejecutar(Articulo articulo) {
        articulo.publicar();
        repository.guardar(articulo);
        return articulo;
    }
}
