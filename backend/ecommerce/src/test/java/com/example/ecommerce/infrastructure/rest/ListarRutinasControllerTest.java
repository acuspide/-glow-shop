package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.response.ListarRutinasResponse;
import com.example.ecommerce.application.usecase.ListarRutinasUseCase;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.infrastructure.rest.mapper.ListarRutinasMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ListarRutinasController.class)
public class ListarRutinasControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListarRutinasUseCase listarRutinasUseCase;

    @MockitoBean
    private ListarRutinasMapper listarRutinasMapper;

    @Test
    void debeRetornarTodasLasRutinas() throws Exception {
        RutinaCuidado rutina = mock(RutinaCuidado.class);

        when(listarRutinasUseCase.ejecutar())
                .thenReturn(List.of(rutina));

        when(listarRutinasMapper.toResponse(List.of(rutina)))
                .thenReturn(
                        new ListarRutinasResponse(
                                List.of(
                                        new ListarRutinasResponse.RutinaResponse(
                                                "r-1",
                                                "Rutina de cuidado diario",
                                                List.of(
                                                        new ListarRutinasResponse.ArticuloResponse(
                                                                "a-1",
                                                                "Shampoo",
                                                                10
                                                        ),
                                                        new ListarRutinasResponse.ArticuloResponse(
                                                                "a-2",
                                                                "Crema hidratante",
                                                                5
                                                        )
                                                )
                                        )
                                )
                        )
                );

        mockMvc.perform(get("/api/rutinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rutinas.length()").value(1))
                .andExpect(jsonPath("$.rutinas[0].id").value("r-1"))
                .andExpect(jsonPath("$.rutinas[0].nombre")
                        .value("Rutina de cuidado diario"))
                .andExpect(jsonPath("$.rutinas[0].articulos.length()").value(2));

        verify(listarRutinasUseCase).ejecutar();
        verify(listarRutinasMapper).toResponse(List.of(rutina));
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayRutinas() throws Exception {
        when(listarRutinasUseCase.ejecutar())
                .thenReturn(List.of());

        when(listarRutinasMapper.toResponse(List.of()))
                .thenReturn(new ListarRutinasResponse(List.of()));

        mockMvc.perform(get("/api/rutinas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rutinas").isArray())
                .andExpect(jsonPath("$.rutinas.length()").value(0));

        verify(listarRutinasUseCase).ejecutar();
        verify(listarRutinasMapper).toResponse(List.of());
    }
}
