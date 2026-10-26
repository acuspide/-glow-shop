package com.example.ecommerce.application.usecase;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.repository.MarcaRepository;
import com.example.ecommerce.domain.service.VerificadorDeMarcaUnica;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CrearMarcaUseCase {
    private final MarcaRepository marcaRepository;
    private final VerificadorDeMarcaUnica verificadorDeMarcaUnica;

    @Transactional
    public Marca ejecutar(String id, String nombre) {
        Marca marca = Marca.crear(id, nombre);                           // el dominio valida y normaliza
        verificadorDeMarcaUnica.verificarQueNoExista(marca.getNombre()); // regla de unicidad
        marcaRepository.guardar(marca);
        return marca;
    }

}
