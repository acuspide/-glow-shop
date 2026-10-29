package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.CrearCategoriaResponse;
import com.example.ecommerce.application.usecase.CrearCategoriaUseCase;
import com.example.ecommerce.domain.entity.Categoria;
import com.example.ecommerce.domain.exception.AccionSoloParaAdministradorException;
import com.example.ecommerce.domain.exception.CategoriaDuplicadaException;
import com.example.ecommerce.infrastructure.rest.mapper.CategoriaMapper;
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

@WebMvcTest(CategoriaController.class)
class CategoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CrearCategoriaUseCase crearCategoriaUseCase;

    @MockitoBean
    private CategoriaMapper mapper;

    @MockitoBean
    private TokenService tokenService;

    @BeforeEach
    void sesionDelAdministrador() {
        when(tokenService.extraerUsuarioId("token-admin")).thenReturn("ad-1");
    }

    @Test
    void deberiaCrearLaCategoriaEnApiCategoriasYRetornar201() throws Exception {
        Categoria categoria = Categoria.crear("cat-1", "Labios");
        when(crearCategoriaUseCase.ejecutar(anyString(), eq("ad-1"), eq("Labios"))).thenReturn(categoria);
        when(mapper.toResponse(categoria)).thenReturn(new CrearCategoriaResponse("cat-1", "Labios"));

        mockMvc.perform(post("/api/categorias").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Labios\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/categorias/cat-1"))
                .andExpect(jsonPath("$.id").value("cat-1"))
                .andExpect(jsonPath("$.nombre").value("Labios"));
    }

    @Test
    void deberiaRetornar401SinToken() throws Exception {
        mockMvc.perform(post("/api/categorias")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Labios\"}"))
                .andExpect(status().isUnauthorized());

        verify(crearCategoriaUseCase, never()).ejecutar(any(), any(), any());
    }

    @Test
    void deberiaRetornar403SiNoEsAdministrador() throws Exception {
        when(crearCategoriaUseCase.ejecutar(anyString(), eq("ad-1"), any()))
                .thenThrow(new AccionSoloParaAdministradorException());

        mockMvc.perform(post("/api/categorias").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Labios\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar400CuandoFaltaElNombre() throws Exception {
        mockMvc.perform(post("/api/categorias").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar409CuandoLaCategoriaYaExiste() throws Exception {
        when(crearCategoriaUseCase.ejecutar(anyString(), eq("ad-1"), any()))
                .thenThrow(new CategoriaDuplicadaException());

        mockMvc.perform(post("/api/categorias").header("Authorization", "Bearer token-admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"Labios\"}"))
                .andExpect(status().isConflict());
    }
}
