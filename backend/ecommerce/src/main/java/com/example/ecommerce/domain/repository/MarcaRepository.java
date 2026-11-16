package com.example.ecommerce.domain.repository;
import com.example.ecommerce.domain.entity.Marca;
import java.util.List;
import java.util.Optional;
public interface MarcaRepository {

    void guardar(Marca marca);

    Optional<Marca> obtenerPorId(String id);

    boolean existePorNombre(String nombre);

    /** Todas las marcas, ordenadas por nombre. */
    List<Marca> listarTodas();
}
