package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.repository.CategoriaRepository;
import com.example.ecommerce.domain.service.VerificadorDeCategoriaUnica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearCategoriaUseCase {
    private final CategoriaRepository categoriaRepository;
    private final VerificadorDeCategoriaUnica verificadorDeCategoriaUnica;

    @Transactional
    public Categoria ejecutar(String id, String nombre) {
        Categoria categoria = Categoria.crear(id, nombre);                       // el dominio valida y normaliza
        verificadorDeCategoriaUnica.verificarQueNoExista(categoria.getNombre()); // regla de unicidad
        categoriaRepository.guardar(categoria);
        return categoria;
    }
}
