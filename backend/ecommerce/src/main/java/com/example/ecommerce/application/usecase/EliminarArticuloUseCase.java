package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.service.VerificadorDeArticuloSinPedidosActivos;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarArticuloUseCase {
    private final ArticuloRepository articuloRepository;
    private final VerificadorDeArticuloSinPedidosActivos verificadorSinPedidosActivos;

    @Transactional
    public Articulo ejecutar(String id, String vendedorId) {
        Articulo articulo = articuloRepository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        articulo.verificarQueEsDelVendedor(vendedorId);
        verificadorSinPedidosActivos.verificarQuePuedeEliminarse(id);
        articulo.eliminar();
        articuloRepository.guardar(articulo);

        return articulo;
    }
}
