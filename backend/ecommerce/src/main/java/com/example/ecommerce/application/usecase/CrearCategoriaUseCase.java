package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import com.example.ecommerce.domain.repository.UsuarioRepository;
import com.example.ecommerce.domain.service.VerificadorDeCategoriaUnica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Crear categoria: solo un administrador activo agrega categorias al catálogo. */
@Service
@RequiredArgsConstructor
public class CrearCategoriaUseCase {
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VerificadorDeCategoriaUnica verificadorDeCategoriaUnica;

    @Transactional
    public Categoria ejecutar(String id, String administradorId, String nombre) {
        Usuario administrador = usuarioRepository.obtenerPorId(administradorId)
                .orElseThrow(AccionSoloParaAdministradorException::new);
        administrador.verificarQueEsAdministradorActivo();

        Categoria categoria = Categoria.crear(id, nombre);                           // el dominio valida y normaliza
        verificadorDeCategoriaUnica.verificarQueNoExista(categoria.getNombre()); // regla de unicidad
        categoriaRepository.guardar(categoria);
        return categoria;
    }
}
