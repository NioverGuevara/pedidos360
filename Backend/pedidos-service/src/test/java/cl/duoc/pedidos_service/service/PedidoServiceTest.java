package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PedidoServiceTest {

    private PedidoRepository pedidoRepository;
    private PedidoService pedidoService;

    @BeforeEach
    void configurar() {
        pedidoRepository = Mockito.mock(PedidoRepository.class);
        pedidoService = new PedidoService(pedidoRepository);
    }

    @Test
    void obtenerTodosDebeRetornarPedidos() {

        Pedido pedido1 = new Pedido(
                "Alexander Rojas",
                "Notebook",
                1,
                "PENDIENTE"
        );

        Pedido pedido2 = new Pedido(
                "Cliente Prueba",
                "Teclado",
                2,
                "EN_PROCESO"
        );

        when(pedidoRepository.findAll())
                .thenReturn(List.of(pedido1, pedido2));

        List<Pedido> resultado = pedidoService.obtenerTodos();

        assertEquals(2, resultado.size());
        assertEquals("Alexander Rojas", resultado.get(0).getCliente());
        assertEquals("Notebook", resultado.get(0).getProducto());
        assertEquals("Cliente Prueba", resultado.get(1).getCliente());
    }
}