package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.application.ParametroInvalidoException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.Pagina;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarArticulosUseCase {


    private static final int TAMANO_MAXIMO = 50;

    private final ArticuloRepository repository;

    @Transactional(readOnly = true)
    public Pagina<Articulo> ejecutar(CriteriosBusquedaArticulo criterios, int pagina, int tamano) {
        if (pagina < 0) {
            throw new ParametroInvalidoException("La página no puede ser negativa");
        }
        if (tamano < 1 || tamano > TAMANO_MAXIMO) {
            throw new ParametroInvalidoException("El tamaño de la página debe estar entre 1 y " + TAMANO_MAXIMO);
        }
        return repository.buscar(criterios, pagina, tamano);
    }

}
