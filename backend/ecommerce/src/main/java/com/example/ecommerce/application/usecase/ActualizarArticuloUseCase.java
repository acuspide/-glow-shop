package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActualizarArticuloUseCase {
    private final ArticuloRepository repository;

    public ActualizarArticuloUseCase(ArticuloRepository repository) {
        this.repository = repository;
    }

    public Articulo ejecutar(
            long id,
            NombreArticulo nombre,
            Precio precio,
            Categoria categoria,
            Marca marca,
            Tono tono,
            List<TipoPiel> tiposPiel,
            FechaVencimiento fechaVencimiento
    ) {
        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.actualizarDatos(
                nombre,
                precio,
                categoria,
                marca,
                tono,
                tiposPiel,
                fechaVencimiento
        );

        repository.guardar(articulo);

        return articulo;
    }
}
