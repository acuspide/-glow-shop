package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.CrearMarcaResponse;
import com.example.ecommerce.application.usecase.CrearMarcaUseCase;
import com.example.ecommerce.domain.entity.Marca;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.MarcaDuplicadaException;
import com.example.ecommerce.infrastructure.rest.mapper.MarcaMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MarcaController.class)
class MarcaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearMarcaUseCase crearMarcaUseCase;

    @MockitoBean
    private MarcaMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDelAdministrador() {
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("ad-1");
    }

    @Test
    void deberiaCrearLaMarcaEnApiMarcasYRetornar201() throws Exception {
        Marca marca = Marca.crear("m-1", "Maybelline");
        when(crearMarcaUseCase.ejecutar(anyString(), eq("ad-1"), eq("Maybelline"))).thenReturn(marca);
        when(mapper.toResponse(marca)).thenReturn(new CrearMarcaResponse("m-1", "Maybelline"));

        mockMvc.perform(post("/api/marcas").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Maybelline\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/marcas/m-1"))
                .andExpect(jsonPath("$.id").value("m-1"))
                .andExpect(jsonPath("$.nombre").value("Maybelline"));
    }

    @Test
    void deberiaRetornar401SinToken() throws Exception {
        mockMvc.perform(post("/api/marcas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Maybelline\"}"))
                .andExpect(status().isUnauthorized());

        verify(crearMarcaUseCase, never()).ejecutar(any(), any(), any());
    }

    @Test
    void deberiaRetornar403SiNoEsAdministrador() throws Exception {
        when(crearMarcaUseCase.ejecutar(anyString(), eq("ad-1"), any()))
                .thenThrow(new AccionSoloParaAdministradorException());

        mockMvc.perform(post("/api/marcas").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Maybelline\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar400CuandoFaltaElNombre() throws Exception {
        mockMvc.perform(post("/api/marcas").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar409CuandoLaMarcaYaExiste() throws Exception {
        when(crearMarcaUseCase.ejecutar(anyString(), eq("ad-1"), any()))
                .thenThrow(new MarcaDuplicadaException());

        mockMvc.perform(post("/api/marcas").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Maybelline\"}"))
                .andExpect(status().isConflict());
    }
}
