package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeMarcaUnica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Crear marca: solo un administrador activo agrega marcas al catálogo. */
@Service
@RequiredArgsConstructor
public class CrearMarcaUseCase {
    private final MarcaRepository marcaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VerificadorDeMarcaUnica verificadorDeMarcaUnica;

    @Transactional
    public Marca ejecutar(String id, String administradorId, String nombre) {
        Usuario administrador = usuarioRepository.obtenerPorId(administradorId)
                .orElseThrow(AccionSoloParaAdministradorException::new);
        administrador.verificarQueEsAdministradorActivo();

        Marca marca = Marca.crear(id, nombre);                           // el dominio valida y normaliza
        verificadorDeMarcaUnica.verificarQueNoExista(marca.getNombre()); // regla de unicidad
        marcaRepository.guardar(marca);
        return marca;
    }
}
