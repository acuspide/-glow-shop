package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.DatosPrueba;
import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.CalificacionResponse;
import com.example.ecommerce.application.dto.response.CalificacionesArticuloResponse;
import com.example.ecommerce.application.usecase.ConsultarCalificacionesArticuloUseCase;
import com.example.ecommerce.domain.valueobject.ResumenCalificaciones;
import com.example.ecommerce.application.usecase.CalificarArticuloUseCase;
import com.example.ecommerce.domain.entity.Calificacion;
import com.example.ecommerce.domain.entity.Usuario;
import com.example.ecommerce.domain.exception.AccionSoloParaClienteException;
import com.example.ecommerce.domain.exception.ArticuloNoRecibidoException;
import com.example.ecommerce.domain.valueobject.Email;
import com.example.ecommerce.domain.valueobject.Puntuacion;
import com.example.ecommerce.infrastructure.rest.mapper.CalificacionMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalificacionController.class)
class CalificacionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalificarArticuloUseCase calificarArticuloUseCase;

    @MockitoBean
    private ConsultarCalificacionesArticuloUseCase consultarCalificacionesUseCase;

    @MockitoBean
    private TokenService tokenService;

    @MockitoBean
    private CalificacionMapper mapper;

    private static final String CALIFICACION_JSON = """
            {
                "puntuacion": 5,
                "comentario": "Me encantó"
            }
            """;

    @Test
    void calificarDebeResponder201ConLaCalificacion() throws Exception {
        // Arrange
        Usuario ana = Usuario.registrarCliente("u-1", "Ana", new Email("ana@correo.com"), "hash", DatosPrueba.TELEFONO, DatosPrueba.FECHA);
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 20, 10, 0);
        Calificacion calificacion = Calificacion.calificar("c-1", 10L, ana, new Puntuacion(5), "Me encantó", fecha);
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");
        when(calificarArticuloUseCase.ejecutar(anyString(), eq("u-1"), eq(10L), eq(5), eq("Me encantó"))).thenReturn(calificacion);
        when(mapper.toResponse(calificacion)).thenReturn(new CalificacionResponse("c-1", 10L, "u-1", 5, "Me encantó", fecha));

        // Act y Assert
        mockMvc.perform(post("/api/articulos/10/calificaciones")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CALIFICACION_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.puntuacion").value(5))
                .andExpect(jsonPath("$.articuloId").value(10));
    }

    @Test
    void unaPuntuacionFueraDeRangoDebeResponder400() throws Exception {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");

        mockMvc.perform(post("/api/articulos/10/calificaciones")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"puntuacion\": 6, \"comentario\": \"Me encantó\"}"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(calificarArticuloUseCase);
    }

    @Test
    void calificarUnArticuloNoRecibidoDebeResponder400() throws Exception {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-1");
        when(calificarArticuloUseCase.ejecutar(anyString(), any(), eq(10L), eq(5), any()))
                .thenThrow(new ArticuloNoRecibidoException());

        mockMvc.perform(post("/api/articulos/10/calificaciones")
                        .header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CALIFICACION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void siQuienCalificaNoEsClienteDebeResponder403() throws Exception {
        when(tokenService.extraerUsuarioId("token-vera")).thenReturn("u-2");
        when(calificarArticuloUseCase.ejecutar(anyString(), eq("u-2"), eq(10L), eq(5), any()))
                .thenThrow(new AccionSoloParaClienteException());

        mockMvc.perform(post("/api/articulos/10/calificaciones")
                        .header("Authorization", "Bearer token-vera")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CALIFICACION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void sinTokenDebeResponder401() throws Exception {
        mockMvc.perform(post("/api/articulos/10/calificaciones")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CALIFICACION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void consultarCalificacionesEsPublicoYDevuelveElPromedio() throws Exception {
        // Arrange
        LocalDateTime fecha = LocalDateTime.of(2026, 10, 20, 10, 0);
        ResumenCalificaciones resumen = new ResumenCalificaciones(10L, List.of());
        when(consultarCalificacionesUseCase.ejecutar(10L)).thenReturn(resumen);
        when(mapper.toResumenResponse(resumen)).thenReturn(new CalificacionesArticuloResponse(10L, 4.5, 2,
                List.of(new CalificacionResponse("c-1", 10L, "u-1", 5, "Excelente", fecha),
                        new CalificacionResponse("c-2", 10L, "u-2", 4, "Muy bueno", fecha))));

        // Act y Assert: sin encabezado Authorization
        mockMvc.perform(get("/api/articulos/10/calificaciones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promedio").value(4.5))
                .andExpect(jsonPath("$.total").value(2))
                .andExpect(jsonPath("$.calificaciones.length()").value(2));
    }

    @Test
    void consultarCalificacionesDeUnArticuloQueNoExisteDebeResponder404() throws Exception {
        when(consultarCalificacionesUseCase.ejecutar(99L))
                .thenThrow(new com.example.ecommerce.domain.exception.ArticuloNoEncontradoException(99L));

        mockMvc.perform(get("/api/articulos/99/calificaciones"))
                .andExpect(status().isNotFound());
    }
}
