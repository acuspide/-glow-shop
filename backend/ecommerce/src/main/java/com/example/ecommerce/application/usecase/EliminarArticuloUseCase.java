package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.ArticuloConPedidosActivosException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EliminarArticuloUseCase {
    private final ArticuloRepository articuloRepository;
    private final PedidoRepository pedidoRepository;

    @Transactional
    public Articulo ejecutar(long id) {
        Articulo articulo = articuloRepository.obtenerPorId(id)
                .orElseThrow(() -> new ArticuloNoEncontradoException(id));

        boolean tienePedidosActivos = pedidoRepository.listarTodos().stream()
                .filter(pedido -> !pedido.getEstado().esFinal())
                .anyMatch(pedido -> pedido.getDetalles().stream()
                        .anyMatch(detalle -> detalle.getArticuloId() == id));

        if (tienePedidosActivos) {
            throw new ArticuloConPedidosActivosException(id);
        }

        articulo.eliminar();
        articuloRepository.guardar(articulo);

        return articulo;
    }
}
