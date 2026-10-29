package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Inventario;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.valueobject.FechaVencimiento;
import com.example.ecommerce.domain.valueobject.NombreArticulo;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.domain.valueobject.TipoPiel;
import com.example.ecommerce.domain.valueobject.Tono;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Publicar artículo: un vendedor publica en SU tienda, y solo si está ACTIVA (RN05, RN10, RN11).
 * La tienda, la categoría y la marca se cargan de sus repositorios: un artículo debe pertenecer
 * a una categoría y a una marca registradas (regla 1 del reto de diseño).
 */
@Service
@RequiredArgsConstructor
public class PublicarArticuloUseCase {

    private final ArticuloRepository articuloRepository;
    private final TiendaRepository tiendaRepository;
    private final CategoriaRepository categoriaRepository;
    private final MarcaRepository marcaRepository;
    private final UsuarioRepository usuarioRepository;

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
            Inventario inventario,
            FechaVencimiento fechaVencimiento,
            String tiendaId) {

        Usuario vendedor = usuarioRepository.obtenerPorId(vendedorId)
                .orElseThrow(AccionSoloParaVendedorException::new);
        vendedor.verificarQueEsVendedorActivo();

        Tienda tienda = tiendaRepository.obtenerPorId(tiendaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la tienda", tiendaId));
        tienda.verificarQuePuedePublicar(vendedorId); // RN10: suya y ACTIVA

        Categoria categoria = categoriaRepository.obtenerPorId(categoriaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la categoría", categoriaId));
        Marca marca = marcaRepository.obtenerPorId(marcaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la marca", marcaId));

        Articulo articulo = Articulo.crear(id, nombre, precio, categoria, marca, tono,
                tiposPiel, inventario, fechaVencimiento, tienda);
        articulo.publicar();

        articuloRepository.guardar(articulo);
        return articulo;
    }
}
