package com.example.ecommerce.infrastructure.persistence;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.exception.ReglaDominioException;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.Pagina;
import com.example.ecommerce.domain.valueobject.CriteriosBusquedaArticulo;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
// Se usa en las pruebas unitarias. En la aplicación corre el adaptador JPA (persistence/jpa).
public class ArticuloRepositoryEnMemoria implements ArticuloRepository {

    private final Map<String, Articulo> articulos = new HashMap<>();

    @Override
    public Optional<Articulo> obtenerPorId(String id) {
        return Optional.ofNullable(articulos.get(id));
    }

    @Override
    public void guardar(Articulo articulo) {
        articulos.put(articulo.getId(), articulo);
    }

    @Override
    public List<Articulo> listarTodos() {
        return List.copyOf(articulos.values());
    }

    // Réplica en memoria de la consulta JPQL del adaptador JPA (solo para pruebas unitarias).
    @Override
    public Pagina<Articulo> buscar(CriteriosBusquedaArticulo criterios, int pagina, int tamano) {
        List<Articulo> coincidencias = articulos.values().stream()
                .filter(this::estaDisponibleParaConsulta)
                .filter(criterios::admite)
                .sorted(Comparator.comparing((Articulo a) -> a.getNombre().valor()).thenComparing(Articulo::getId))
                .toList();

        int desde = (int) Math.min((long) pagina * tamano, coincidencias.size());
        int hasta = Math.min(desde + tamano, coincidencias.size());
        int totalPaginas = (int) Math.ceil((double) coincidencias.size() / tamano);

        return new Pagina<>(coincidencias.subList(desde, hasta), coincidencias.size(), totalPaginas, pagina, tamano);
    }

    private boolean estaDisponibleParaConsulta(Articulo articulo) {
        try {
            articulo.validarDisponibleParaConsulta();
            return true;
        } catch (ReglaDominioException e) {
            return false;
        }
    }
}
