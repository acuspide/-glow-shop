package com.example.ecommerce.infrastructure.config;

import com.example.ecommerce.domain.repository.CalificacionRepository;
import com.example.ecommerce.domain.repository.HistorialDeCompras;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCalificacion;
import com.example.ecommerce.domain.service.VerificadorDeCorreoUnico;
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
}
