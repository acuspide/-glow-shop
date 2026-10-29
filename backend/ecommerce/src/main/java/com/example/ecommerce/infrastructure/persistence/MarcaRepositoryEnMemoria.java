package com.example.ecommerce.infrastructure.persistence;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.repository.MarcaRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class MarcaRepositoryEnMemoria implements MarcaRepository {

    private final Map<String, Marca> marcas = new HashMap<>();

    @Override
    public void guardar(Marca marca) {
        marcas.put(marca.getId(), marca);
    }

    @Override
    public Optional<Marca> obtenerPorId(String id) {

        return Optional.ofNullable(marcas.get(id));
    }

    @Override
    public boolean existePorNombre(String nombre) {
        String buscado = nombre.trim();
        return marcas.values().stream()
                .anyMatch(marca ->
                        marca.getNombre().equalsIgnoreCase(nombre.trim()));
    }
}
