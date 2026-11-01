package com.example.ecommerce.integracion;

import com.example.ecommerce.domain.entity.Articulo;
import com.example.ecommerce.domain.entity.Pedido;
import com.example.ecommerce.domain.repository.ArticuloRepository;
import com.example.ecommerce.domain.repository.PedidoRepository;
import com.example.ecommerce.domain.valueobject.EstadoPedido;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Prueba de integración: recorre el sistema real sin simular nada (sin @MockitoBean).
 * La petición entra por el controller, pasa por el DTO, el caso de uso y el dominio, y llega
 * a los repositorios JPA contra H2 con los datos de data.sql. @Transactional deshace todo al final.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ComprarArticuloIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ArticuloRepository articuloRepository;

    @Autowired
    private PedidoRepository pedidoRepository;

    private String tokenDe(String email) throws Exception {
        String respuesta = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"contrasena\": \"Clave12345\"}"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return "Bearer " + respuesta.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");
    }

    @Test
    void laClientaCompraUnLabialYSeCreaElPedidoYSeDescuentaElInventario() throws Exception {
        // Arrange: Carlos (cliente de data.sql) y el labial de Glow Natural, que tiene 25 unidades
        String carlos = tokenDe("carlos@correo.com");
        int inventarioAntes = articuloRepository.obtenerPorId("art-labial-mate").orElseThrow().getCantidadDisponible();
        int pedidosAntes = pedidoRepository.obtenerPorClienteId("usr-carlos").size();

        // Act: agregar 2 labiales al carrito y confirmar la compra
        mockMvc.perform(post("/api/carrito/items").header("Authorization", carlos)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"articuloId\": \"art-labial-mate\", \"cantidad\": 2}"))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/pedidos").header("Authorization", carlos))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADO"))
                .andExpect(jsonPath("$[0].tiendaId").value("tienda-glow-natural"))
                .andExpect(jsonPath("$[0].total").value(70000));

        // Assert: lo que quedó guardado en la base de datos
        Articulo labial = articuloRepository.obtenerPorId("art-labial-mate").orElseThrow();
        assertEquals(inventarioAntes - 2, labial.getCantidadDisponible());

        List<Pedido> pedidosDeCarlos = pedidoRepository.obtenerPorClienteId("usr-carlos");
        assertEquals(pedidosAntes + 1, pedidosDeCarlos.size());
        assertEquals(1, pedidosDeCarlos.stream().filter(p -> p.getEstado() == EstadoPedido.CONFIRMADO).count());
    }

    @Test
    void laVendedoraNoPuedeSaltarDeConfirmadoAEntregadoYElPedidoNoCambia() throws Exception {
        // Arrange: ped-005 de data.sql está CONFIRMADO y es de la tienda de Vera
        String vera = tokenDe("vera@glow.com");

        // Act y Assert: regla de negocio rota -> 422 con el mensaje del dominio
        mockMvc.perform(put("/api/vendedor/pedidos/ped-005/estado").header("Authorization", vera)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"estado\": \"ENTREGADO\"}"))
                .andExpect(status().isUnprocessableContent())
                .andExpect(jsonPath("$.mensaje").value("No se puede pasar de CONFIRMADO a ENTREGADO."));

        assertEquals(EstadoPedido.CONFIRMADO, pedidoRepository.obtenerPorId("ped-005").orElseThrow().getEstado());
    }
}
