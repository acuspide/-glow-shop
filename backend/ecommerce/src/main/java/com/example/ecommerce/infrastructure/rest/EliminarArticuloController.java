package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.usecase.EliminarArticuloUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/articulos")
public class EliminarArticuloController {
    private final EliminarArticuloUseCase eliminarArticuloUseCase;

    public EliminarArticuloController(
            EliminarArticuloUseCase eliminarArticuloUseCase
    ) {
        this.eliminarArticuloUseCase = eliminarArticuloUseCase;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable long id) {
        eliminarArticuloUseCase.ejecutar(id);

        return ResponseEntity.noContent().build();
    }
}
