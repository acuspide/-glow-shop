package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.dto.response.ConsultarInventarioCompletoResponse;
import com.example.ecommerce.application.usecase.ConsultarInventarioCompletoUseCase;
import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.infrastructure.rest.mapper.ConsultarInventarioCompletoMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConsultarInventarioController.class)
public class ConsultarInventarioControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ConsultarInventarioCompletoUseCase consultarInventarioCompletoUseCase;

    @MockitoBean
    private ConsultarInventarioCompletoMapper consultarInventarioCompletoMapper;

    @Test
    void debeMostrarElInventarioCompleto() throws Exception {
        List<Articulo> articulos = List.of(
                org.mockito.Mockito.mock(Articulo.class),
                org.mockito.Mockito.mock(Articulo.class)
        );

        var response = new ConsultarInventarioCompletoResponse(
                List.of(
                        new ConsultarInventarioCompletoResponse.ArticuloInventarioResponse(
                                "a-1", "Labial rojo", 10
                        ),
                        new ConsultarInventarioCompletoResponse.ArticuloInventarioResponse(
                                "a-2", "Crema hidratante", 0
                        )
                )
        );

        when(consultarInventarioCompletoUseCase.ejecutar())
                .thenReturn(articulos);
        when(consultarInventarioCompletoMapper.toResponse(articulos))
                .thenReturn(response);

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articulos.length()").value(2))
                .andExpect(jsonPath("$.articulos[0].nombre").value("Labial rojo"))
                .andExpect(jsonPath("$.articulos[0].cantidadDisponible").value(10))
                .andExpect(jsonPath("$.articulos[1].nombre").value("Crema hidratante"))
                .andExpect(jsonPath("$.articulos[1].cantidadDisponible").value(0));
    }

    @Test
    void debeRetornarListaVaciaCuandoNoHayArticulos() throws Exception {
        when(consultarInventarioCompletoUseCase.ejecutar())
                .thenReturn(List.of());

        when(consultarInventarioCompletoMapper.toResponse(List.of()))
                .thenReturn(new ConsultarInventarioCompletoResponse(List.of()));

        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.articulos").isEmpty());
    }
}
