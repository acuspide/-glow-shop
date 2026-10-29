package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Tienda;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaVendedorException;
import com.example.ecommerce.domain.repository.TiendaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeTiendaUnicaPorVendedor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Registrar tienda: el vendedor con sesión iniciada solicita abrir su tienda.
 * Queda PENDIENTE hasta que un administrador la apruebe (RN05).
 */
@Service
@RequiredArgsConstructor
public class RegistrarTiendaUseCase {

    private final UsuarioRepository usuarioRepository;
    private final TiendaRepository tiendaRepository;
    private final VerificadorDeTiendaUnicaPorVendedor verificadorDeTiendaUnica;

    @Transactional
    public Tienda ejecutar(String id, String vendedorId, String nombre, String descripcion) {
        Usuario vendedor = usuarioRepository.obtenerPorId(vendedorId)
                .orElseThrow(AccionSoloParaVendedorException::new);

        Tienda tienda = Tienda.registrar(id, nombre, descripcion, vendedor); // el dominio valida rol y datos
        verificadorDeTiendaUnica.verificarQueNoTengaTienda(vendedorId);      // RN08

        tiendaRepository.guardar(tienda);
        return tienda;
    }
}
