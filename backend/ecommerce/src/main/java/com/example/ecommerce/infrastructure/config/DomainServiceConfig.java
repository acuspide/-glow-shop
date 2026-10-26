package com.example.ecommerce.infrastructure.config;

import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.HistorialDeCompras;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeArticuloSinPedidosActivos;
import com.example.ecommerce.domain.service.VerificadorDeCalificacion;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
import com.example.ecommerce.domain.service.VerificadorDeMarcaUnica;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.service.VerificadorDeDisponibilidadDelCarrito;
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
}
