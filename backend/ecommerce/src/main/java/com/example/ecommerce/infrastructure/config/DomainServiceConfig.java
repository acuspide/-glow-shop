package com.example.ecommerce.infrastructure.config;

import com.example.ecommerce.domain.repository.*;
import com.example.ecommerce.domain.service.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Los servicios de dominio no llevan anotaciones de Spring; aquí se le indica a Spring cómo crearlos.
 */
@Configuration
public class DomainServiceConfig {

    @Bean
    public VerificadorDeCorreoUnico verificadorDeCorreoUnico(UsuarioRepository usuarioRepository) {
        return new VerificadorDeCorreoUnico(usuarioRepository);
    }

    @Bean
    public VerificadorDeCalificacion verificadorDeCalificacion(HistorialDeCompras historialDeCompras,
                                                               CalificacionRepository calificacionRepository) {
        return new VerificadorDeCalificacion(historialDeCompras, calificacionRepository);
    }

    @Bean
    public VerificadorDeArticuloSinPedidosActivos verificadorDeArticuloSinPedidosActivos(PedidoRepository pedidoRepository) {
        return new VerificadorDeArticuloSinPedidosActivos(pedidoRepository);
    }
    @Bean
    public VerificadorDeDisponibilidadDelCarrito verificadorDeDisponibilidadDelCarrito(ArticuloRepository articuloRepository) {
        return new VerificadorDeDisponibilidadDelCarrito(articuloRepository);
    }
    @Bean
    public VerificadorDeMarcaUnica verificadorDeMarcaUnica(MarcaRepository marcaRepository) {
        return new VerificadorDeMarcaUnica(marcaRepository);
    }
    @Bean
    public VerificadorDeCategoriaUnica verificadorDeCategoriaUnica(CategoriaRepository categoriaRepository) {
        return new VerificadorDeCategoriaUnica(categoriaRepository);
    }

    @Bean
    public VerificadorDeTiendaUnicaPorVendedor verificadorDeTiendaUnicaPorVendedor(TiendaRepository tiendaRepository) {
        return new VerificadorDeTiendaUnicaPorVendedor(tiendaRepository);
    }
}
