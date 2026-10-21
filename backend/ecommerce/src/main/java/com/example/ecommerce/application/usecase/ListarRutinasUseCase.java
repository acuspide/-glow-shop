package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListarRutinasUseCase {
    private final RutinaCuidadoRepository rutinaRepository;

    public ListarRutinasUseCase(
            RutinaCuidadoRepository rutinaRepository) {
        this.rutinaRepository = rutinaRepository;
    }

    public List<RutinaCuidado> ejecutar() {
        return rutinaRepository.listarTodas();
    }
}
