package com.example.ecommerce.infrastructure.rest;

import com.example.ecommerce.application.TokenService;
import com.example.ecommerce.application.dto.response.PagoResponse;
import com.example.ecommerce.application.usecase.ProcesarPagoDePedidoUseCase;
import com.example.ecommerce.application.usecase.ReintentarPagoUseCase;
import com.example.ecommerce.domain.entity.Pago;
import com.example.ecommerce.domain.exception.PagoNoRegistrableException;
import com.example.ecommerce.domain.exception.PagoNoReintentableException;
import com.example.ecommerce.domain.exception.PedidoAjenoException;
import com.example.ecommerce.domain.valueobject.MedioPago;
import com.example.ecommerce.domain.valueobject.Precio;
import com.example.ecommerce.infrastructure.rest.mapper.PagoMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.example.ecommerce.application.usecase.ConsultarEstadoDePagoUseCase;
import com.example.ecommerce.application.usecase.ListarHistorialDePagosUseCase;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(PagoController.class)
class PagoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProcesarPagoDePedidoUseCase procesarPagoDePedidoUseCase;

    @MockitoBean
    private ConsultarEstadoDePagoUseCase consultarEstadoDePagoUseCase;

    @MockitoBean
    private ListarHistorialDePagosUseCase listarHistorialDePagosUseCase;

    @MockitoBean
    private ReintentarPagoUseCase reintentarPagoUseCase;      // ← este

    @MockitoBean
    private PagoMapper mapper;

    @MockitoBean
    private TokenService tokenService;
    private static final String CON_NEQUI = """
            {
                "medioPago": "NEQUI"
            }
            """;

    @BeforeEach
    void sesionDeAna() {
        when(tokenService.extraerUsuarioId("token-ana")).thenReturn("u-100");
    }

    private Pago pagoSimulado() {
        return Pago.crear("pa-1", "p-1", new Precio(new BigDecimal("40000")), MedioPago.NEQUI);
    }

    @Test
    void deberiaProcesarElPagoYRetornar201ConElPagoAprobado() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), eq("p-1"), eq("u-100"), eq(MedioPago.NEQUI)))
                .thenReturn(pagoSimulado());
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-1", "p-1", new BigDecimal("40000"), "NEQUI", null,
                        "APROBADO", "tx-1", null));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isCreated())                    // HTTP 201
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("APROBADO"));

        verify(procesarPagoDePedidoUseCase).ejecutar(anyString(), eq("p-1"), eq("u-100"), eq(MedioPago.NEQUI));
    }

    @Test
    void unPagoRechazadoTambienRetorna201ConSuMotivo() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenReturn(pagoSimulado());
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-1", "p-1", new BigDecimal("1500000"), "NEQUI", null,
                        "RECHAZADO", "tx-2", "Fondos insuficientes"));

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.estado").value("RECHAZADO"))
                .andExpect(jsonPath("$.motivoRechazo").value("Fondos insuficientes"));
    }

    @Test
    void deberiaRetornar400CuandoNoSeEnviaElMedioDePago() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());

        verify(procesarPagoDePedidoUseCase, never()).ejecutar(anyString(), anyString(), anyString(), any());
    }

    @Test
    void deberiaRetornar400CuandoElPedidoNoAceptaPago() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PagoNoRegistrableException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar403CuandoElPedidoEsDeOtroCliente() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PedidoAjenoException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar404CuandoElPedidoNoExiste() throws Exception {

        // Arrange
        when(procesarPagoDePedidoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-99/pagos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isNotFound());
    }

    @Test
    void sinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pedidos/p-1/pagos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CON_NEQUI))
                .andExpect(status().isUnauthorized());

        verify(procesarPagoDePedidoUseCase, never()).ejecutar(anyString(), anyString(), anyString(), any());
    }
    @Test
    void deberiaMostrarElEstadoDelPagoConSuMotivo() throws Exception {

        // Arrange
        when(consultarEstadoDePagoUseCase.ejecutar("pa-1", "u-100")).thenReturn(pagoSimulado());
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-1", "p-1", new BigDecimal("1500000"), "NEQUI", null,
                        "RECHAZADO", "tx-2", "Fondos insuficientes"));

        // Act y Assert
        mockMvc.perform(get("/api/pagos/pa-1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())                         // HTTP 200
                .andExpect(jsonPath("$.estado").value("RECHAZADO"))
                .andExpect(jsonPath("$.motivoRechazo").value("Fondos insuficientes"));
    }

    @Test
    void deberiaRetornar403AlConsultarElPagoDeOtraClienta() throws Exception {

        // Arrange
        when(consultarEstadoDePagoUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new PedidoAjenoException());

        // Act y Assert
        mockMvc.perform(get("/api/pagos/pa-1").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar404CuandoElPagoNoExiste() throws Exception {

        // Arrange
        when(consultarEstadoDePagoUseCase.ejecutar(anyString(), anyString()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(get("/api/pagos/pa-99").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void consultarSinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(get("/api/pagos/pa-1"))
                .andExpect(status().isUnauthorized());

        verify(consultarEstadoDePagoUseCase, never()).ejecutar(anyString(), anyString());
    }

    @Test
    void deberiaListarElHistorialDePagosDeLaClienta() throws Exception {

        // Arrange
        when(listarHistorialDePagosUseCase.ejecutar("u-100")).thenReturn(List.of(pagoSimulado(), pagoSimulado()));
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-1", "p-1", new BigDecimal("40000"), "NEQUI", null,
                        "APROBADO", "tx-1", null));

        // Act y Assert
        mockMvc.perform(get("/api/pagos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())                         // HTTP 200
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].estado").value("APROBADO"));
    }

    @Test
    void deberiaRetornarUnaListaVaciaSiLaClientaNoTienePagos() throws Exception {

        // Arrange
        when(listarHistorialDePagosUseCase.ejecutar("u-100")).thenReturn(List.of());

        // Act y Assert
        mockMvc.perform(get("/api/pagos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void listarHistorialSinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(get("/api/pagos"))
                .andExpect(status().isUnauthorized());

        verify(listarHistorialDePagosUseCase, never()).ejecutar(anyString());
    }

    @Test
    void deberiaReintentarConElMismoMedioCuandoNoSeEnviaCuerpo() throws Exception {

        // Arrange
        when(reintentarPagoUseCase.ejecutar(anyString(), eq("pa-1"), eq("u-100"), isNull()))
                .thenReturn(pagoSimulado());
        when(mapper.toResponse(any()))
                .thenReturn(new PagoResponse("pa-2", "p-1", new BigDecimal("40000"), "NEQUI", null,
                        "APROBADO", "tx-2", null));

        // Act y Assert
        mockMvc.perform(post("/api/pagos/pa-1/reintentos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isCreated())                    // HTTP 201
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.estado").value("APROBADO"));

        verify(reintentarPagoUseCase).ejecutar(anyString(), eq("pa-1"), eq("u-100"), isNull());
    }

    @Test
    void deberiaReintentarConOtroMedioCuandoSeEnvia() throws Exception {

        // Arrange
        String conTarjeta = """
                {
                    "medioPago": "TARJETA_CREDITO"
                }
                """;
        when(reintentarPagoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenReturn(pagoSimulado());

        // Act y Assert
        mockMvc.perform(post("/api/pagos/pa-1/reintentos").header("Authorization", "Bearer token-ana")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conTarjeta))
                .andExpect(status().isCreated());

        verify(reintentarPagoUseCase).ejecutar(anyString(), eq("pa-1"), eq("u-100"), eq(MedioPago.TARJETA_CREDITO));
    }

    @Test
    void deberiaRetornar400CuandoElPagoNoFueRechazado() throws Exception {

        // Arrange
        when(reintentarPagoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PagoNoReintentableException());

        // Act y Assert
        mockMvc.perform(post("/api/pagos/pa-1/reintentos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deberiaRetornar403AlReintentarElPagoDeOtraClienta() throws Exception {

        // Arrange
        when(reintentarPagoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new PedidoAjenoException());

        // Act y Assert
        mockMvc.perform(post("/api/pagos/pa-1/reintentos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isForbidden());
    }

    @Test
    void deberiaRetornar404AlReintentarUnPagoQueNoExiste() throws Exception {

        // Arrange
        when(reintentarPagoUseCase.ejecutar(anyString(), anyString(), anyString(), any()))
                .thenThrow(new NoSuchElementException());

        // Act y Assert
        mockMvc.perform(post("/api/pagos/pa-99/reintentos").header("Authorization", "Bearer token-ana"))
                .andExpect(status().isNotFound());
    }

    @Test
    void reintentarSinTokenDebeResponder401() throws Exception {

        // Act y Assert
        mockMvc.perform(post("/api/pagos/pa-1/reintentos"))
                .andExpect(status().isUnauthorized());

        verify(reintentarPagoUseCase, never()).ejecutar(anyString(), anyString(), anyString(), any());
    }
}
