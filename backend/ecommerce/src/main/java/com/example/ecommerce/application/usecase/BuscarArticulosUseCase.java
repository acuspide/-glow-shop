package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.Pagina;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;
import com.example.ecommerce.domain.valueobject.Paginacion;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BuscarArticulosUseCase {

    private final ArticuloRepository repository;

    @Transactional(readOnly = true)
    public Pagina<Articulo> ejecutar(CriteriosBusquedaArticulo criterios, int pagina, int tamano) {
        // Las reglas de página y tamaño las valida el Value Object Paginacion.
        Paginacion paginacion = new Paginacion(pagina, tamano);
        return repository.buscar(criterios, paginacion.pagina(), paginacion.tamano());
    }

}
