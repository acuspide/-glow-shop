package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;

import java.util.List;

public class CrearRutinaCuidadoUseCase {
    private final RutinaCuidadoRepository repository;

    public CrearRutinaCuidadoUseCase(RutinaCuidadoRepository repository) {
        this.repository = repository;
    }

    public void ejecutar(
            long id,
            String nombre,
            List<Articulo> articulos) {

        RutinaCuidado rutina = new RutinaCuidado(
                id,
                nombre,
                articulos
        );

        repository.guardar(rutina);
    }

}