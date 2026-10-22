package com.example.ecommerce.application.usecase;

import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.domain.repository.RutinaCuidadoRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarRutinasUseCase {
    private final RutinaCuidadoRepository rutinaRepository;

    @Transactional(readOnly = true)
    public List<RutinaCuidado> ejecutar() {
        return rutinaRepository.listarTodas();
    }
}
