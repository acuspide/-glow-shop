package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.ActualizarRutinaCuidadoRequest;
import com.example.ecommerce.application.dto.request.CrearRutinaCuidadoRequest;
import com.example.ecommerce.application.dto.response.RutinaCuidadoResponse;
import com.example.ecommerce.application.usecase.ActualizarRutinaCuidadoUseCase;
import com.example.ecommerce.application.usecase.CrearRutinaCuidadoUseCase;
import com.example.ecommerce.application.usecase.ObtenerRutinaCuidadoUseCase;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.infrastructure.rest.mapper.RutinaCuidadoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RutinaCuidadoController.class)
public class RutinaCuidadoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearRutinaCuidadoUseCase crearRutinaCuidadoUseCase;

    @MockitoBean
    private ObtenerRutinaCuidadoUseCase obtenerRutinaCuidadoUseCase;

    @MockitoBean
    private ActualizarRutinaCuidadoUseCase actualizarRutinaCuidadoUseCase;

    @MockitoBean
    private RutinaCuidadoMapper mapper;

    @Test
    void debeActualizarUnaRutina() throws Exception {
        RutinaCuidado rutina = mock(RutinaCuidado.class);

        RutinaCuidadoResponse response = mock(RutinaCuidadoResponse.class);

        when(actualizarRutinaCuidadoUseCase.ejecutar(
                1L,
                "Rutina actualizada",
                List.of(10L, 20L)
        )).thenReturn(rutina);

        when(mapper.toResponse(rutina)).thenReturn(response);

        mockMvc.perform(put("/api/rutinas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Rutina actualizada",
                                  "articulosIds": [10, 20]
                                }
                                """))
                .andExpect(status().isOk());

        verify(actualizarRutinaCuidadoUseCase).ejecutar(
                1L,
                "Rutina actualizada",
                List.of(10L, 20L)
        );

        verify(mapper).toResponse(rutina);
    }

    @Test
    void debeRechazarActualizacionConNombreVacio() throws Exception {
        mockMvc.perform(put("/api/rutinas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "articulosIds": [10, 20]
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(actualizarRutinaCuidadoUseCase);
    }

    @Test
    void debeRechazarActualizacionConMenosDeDosArticulos() throws Exception {
        mockMvc.perform(put("/api/rutinas/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Rutina actualizada",
                                  "articulosIds": [10]
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(actualizarRutinaCuidadoUseCase);
    }
}
