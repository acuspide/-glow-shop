package com.example.ecommerce.infrastructure.rest;
import com.example.ecommerce.application.dto.request.ActualizarRutinaCuidadoRequest;
import com.example.ecommerce.application.dto.request.CrearRutinaCuidadoRequest;
import com.example.ecommerce.application.dto.response.RutinaCuidadoResponse;
import com.example.ecommerce.application.usecase.ActualizarRutinaCuidadoUseCase;
import com.example.ecommerce.application.usecase.CrearRutinaCuidadoUseCase;
import com.example.ecommerce.application.usecase.ObtenerRutinaCuidadoUseCase;
import com.example.ecommerce.domain.entity.RutinaCuidado;
import com.example.ecommerce.infrastructure.rest.mapper.RutinaCuidadoMapper;
import com.example.ecommerce.application.usecase.AgregarArticuloARutinaUseCase;
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

    @MockitoBean
    private AgregarArticuloARutinaUseCase agregarArticuloARutinaUseCase;

    @Test
    void debeActualizarUnaRutina() throws Exception {
        RutinaCuidado rutina = mock(RutinaCuidado.class);

        RutinaCuidadoResponse response = mock(RutinaCuidadoResponse.class);

        when(actualizarRutinaCuidadoUseCase.ejecutar(
                "r-1",
                "Rutina actualizada",
                List.of("a-10", "a-20")
        )).thenReturn(rutina);

        when(mapper.toResponse(rutina)).thenReturn(response);

        mockMvc.perform(put("/api/rutinas/r-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Rutina actualizada",
                                  "articulosIds": ["a-10", "a-20"]
                                }
                                """))
                .andExpect(status().isOk());

        verify(actualizarRutinaCuidadoUseCase).ejecutar(
                "r-1",
                "Rutina actualizada",
                List.of("a-10", "a-20")
        );

        verify(mapper).toResponse(rutina);
    }

    @Test
    void debeRechazarActualizacionConNombreVacio() throws Exception {
        mockMvc.perform(put("/api/rutinas/r-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "articulosIds": ["a-10", "a-20"]
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(actualizarRutinaCuidadoUseCase);
    }

    @Test
    void debeRechazarActualizacionConMenosDeDosArticulos() throws Exception {
        mockMvc.perform(put("/api/rutinas/r-1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Rutina actualizada",
                                  "articulosIds": ["a-10"]
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(actualizarRutinaCuidadoUseCase);
    }

    @Test
    void debeAgregarArticuloAUnaRutina() throws Exception {
        RutinaCuidado rutina = mock(RutinaCuidado.class);

        RutinaCuidadoResponse response = mock(RutinaCuidadoResponse.class);

        when(agregarArticuloARutinaUseCase.ejecutar("r-1", "a-3"))
                .thenReturn(rutina);

        when(mapper.toResponse(rutina)).thenReturn(response);

        mockMvc.perform(post("/api/rutinas/r-1/articulos/a-3"))
                .andExpect(status().isOk());

        verify(agregarArticuloARutinaUseCase).ejecutar("r-1", "a-3");

        verify(mapper).toResponse(rutina);
    }

}
