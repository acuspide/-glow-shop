package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * Actualizar artículo: el vendedor dueño de la tienda cambia los datos del artículo.
 * El inventario no se toca aquí (tiene su propio caso de uso).
 */
@Service
@RequiredArgsConstructor
public class ActualizarArticuloUseCase {
    private final ArticuloRepository repository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;

    @Transactional
    public Articulo ejecutar(
            String id,
            String vendedorId,
            NombreArticulo nombre,
            Precio precio,
            String categoriaId,
            String marcaId,
            Tono tono,
            List<TipoPiel> tiposPiel,
            FechaVencimiento fechaVencimiento
    ) {
        Articulo articulo = repository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));
        articulo.verificarQueEsDelVendedor(vendedorId);

        Categoria categoria = categoriaRepository.obtenerPorId(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la categoría", categoriaId));
        Marca marca = marcaRepository.obtenerPorId(marcaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la marca", marcaId));

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
