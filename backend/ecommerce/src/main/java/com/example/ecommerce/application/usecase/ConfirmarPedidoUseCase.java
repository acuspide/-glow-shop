package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.service.VerificadorDeDisponibilidadDelCarrito;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.ItemCarrito;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfirmarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ArticuloRepository articuloRepository;
    private final VerificadorDeDisponibilidadDelCarrito verificador;

    @Transactional
    public Pedido ejecutar(String pedidoId, String clienteId) {
        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId).orElseThrow(() -> new RecursoNoEncontradoException("el carrito del cliente", clienteId));

        // Se valida TODO el carrito antes de modificar cualquier cosa (CU-57).
        verificador.verificarQuePuedePagarse(carrito);

        Pedido pedido = Pedido.crear(pedidoId, clienteId, convertirEnDetalles(carrito));
        pedido.confirmar();

        descontarInventario(carrito);
        carrito.vaciar();

        pedidoRepository.guardar(pedido);
        carritoRepository.guardar(carrito);

        return pedido;
    }

    // El precio sale del carrito: el verificador ya comprobó que sigue vigente.
    private List<DetallePedido> convertirEnDetalles(Carrito carrito) {
        return carrito.getItems().stream()
                .map(item -> new DetallePedido(item.getArticuloId(), item.getCantidad(), item.getPrecioUnitario()))
                .toList();
    }

    private void descontarInventario(Carrito carrito) {
        for (ItemCarrito item : carrito.getItems()) {
            Articulo articulo = articuloRepository.obtenerPorId(item.getArticuloId()).orElseThrow(() -> new ArticuloNoEncontradoException(item.getArticuloId()));
            articulo.disminuirInventario(item.getCantidad());
            articuloRepository.guardar(articulo);
        }
    }
}