package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.RecursoNoEncontradoException;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Aprobar tienda (RN05, RN06): un administrador pasa una tienda de PENDIENTE a ACTIVA.
 * Desde ese momento su vendedor puede publicar artículos (RN10).
 */
@Service
@RequiredArgsConstructor
public class AprobarTiendaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TiendaRepository tiendaRepository;

    @Transactional
    public Tienda ejecutar(String tiendaId, String administradorId) {
        Usuario administrador = usuarioRepository.obtenerPorId(administradorId)
                .orElseThrow(AccionSoloParaAdministradorException::new);
        Tienda tienda = tiendaRepository.obtenerPorId(tiendaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("la tienda", tiendaId));

        tienda.aprobarPor(administrador); // el dominio verifica el rol y que siga pendiente

        tiendaRepository.guardar(tienda);
        return tienda;
    }
}
