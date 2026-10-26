package com.example.ecommerce.domain.service;
import com.example.ecommerce.domain.exception.MarcaDuplicadaException;
import com.example.ecommerce.domain.repository.MarcaRepository;

/**
 * Servicio de dominio: el nombre de la marca es único (sin distinguir mayúsculas).
 * La regla necesita mirar TODAS las marcas (el repositorio), por eso no cabe en la entidad Marca.
 */
public class VerificadorDeMarcaUnica {
    private final MarcaRepository marcaRepository;

    public VerificadorDeMarcaUnica(MarcaRepository marcaRepository) {
        this.marcaRepository = marcaRepository;
    }

    public void verificarQueNoExista(String nombre) {
        if (marcaRepository.existePorNombre(nombre)) {
            throw new MarcaDuplicadaException();
        }
    }
}
