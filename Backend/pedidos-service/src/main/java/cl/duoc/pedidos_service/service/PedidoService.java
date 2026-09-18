package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.model.Pedido;
import cl.duoc.pedidos_service.repository.PedidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public List<Pedido> obtenerTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<Pedido> obtenerPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public Pedido crear(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Optional<Pedido> actualizar(Long id, Pedido pedidoActualizado) {

        return pedidoRepository.findById(id).map(pedido -> {

            pedido.setCliente(pedidoActualizado.getCliente());
            pedido.setProducto(pedidoActualizado.getProducto());
            pedido.setCantidad(pedidoActualizado.getCantidad());
            pedido.setEstado(pedidoActualizado.getEstado());

            return pedidoRepository.save(pedido);
        });
    }

    public boolean eliminar(Long id) {

        if (!pedidoRepository.existsById(id)) {
            return false;
        }

        pedidoRepository.deleteById(id);
        return true;
    }
}