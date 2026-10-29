package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Carrito;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.exception.ArticuloNoEncontradoException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.CarritoRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.service.VerificadorDeDisponibilidadDelCarrito;
import com.example.ecommerce.domain.valueobject.DetallePedido;
import com.example.ecommerce.domain.valueobject.ItemCarrito;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Confirmar pedido: convierte el carrito en pedidos, UNO POR CADA TIENDA del carrito.
 * Así el vendedor de cada tienda despacha y entrega solo lo suyo.
 */
@Service
@RequiredArgsConstructor
public class ConfirmarPedidoUseCase {

    private final PedidoRepository pedidoRepository;
    private final CarritoRepository carritoRepository;
    private final ArticuloRepository articuloRepository;
    private final VerificadorDeDisponibilidadDelCarrito verificador;

    /** @param nuevoPedidoId entrega un id nuevo por cada pedido (los genera el controller). */
    @Transactional
    public List<Pedido> ejecutar(String clienteId, Supplier<String> nuevoPedidoId) {
        Carrito carrito = carritoRepository.obtenerPorClienteId(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("el carrito del cliente", clienteId));

        // Se valida TODO el carrito antes de modificar cualquier cosa (CU-57).
        verificador.verificarQuePuedePagarse(carrito);

        List<Pedido> pedidos = detallesPorTienda(carrito).entrySet().stream()
                .map(tienda -> Pedido.crear(nuevoPedidoId.get(), clienteId, tienda.getKey(), tienda.getValue()))
                .toList();
        pedidos.forEach(Pedido::confirmar);

        descontarInventario(carrito);
        carrito.vaciar();

        pedidos.forEach(pedidoRepository::guardar);
        carritoRepository.guardar(carrito);

        return pedidos;
    }

    // Agrupa los ítems del carrito por la tienda de cada artículo, conservando el orden del carrito.
    // El precio sale del carrito: el verificador ya comprobó que sigue vigente.
    private Map<String, List<DetallePedido>> detallesPorTienda(Carrito carrito) {
        return carrito.getItems().stream()
                .collect(Collectors.groupingBy(
                        item -> buscarArticulo(item.getArticuloId()).getTienda().getId(),
                        LinkedHashMap::new,
                        Collectors.mapping(
                                item -> new DetallePedido(item.getArticuloId(), item.getCantidad(), item.getPrecioUnitario()),
                                Collectors.toList())));
    }

    private void descontarInventario(Carrito carrito) {
        for (ItemCarrito item : carrito.getItems()) {
            Articulo articulo = buscarArticulo(item.getArticuloId());
            articulo.disminuirInventario(item.getCantidad());
            articuloRepository.guardar(articulo);
        }
    }

    private Articulo buscarArticulo(String articuloId) {
        return articuloRepository.obtenerPorId(articuloId)
                .orElseThrow(() -> new ArticuloNoEncontradoException(articuloId));
    }
}
