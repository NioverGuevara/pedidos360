
package cl.duoc.pedidos_service.service;

import cl.duoc.pedidos_service.RabbitMQConfig;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class PedidoProducer {

    private final RabbitTemplate rabbitTemplate;

    public PedidoProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarPedidoCreado(Long pedidoId) {

        String mensaje = "Pedido creado con ID: " + pedidoId;

        System.out.println("PUBLICANDO MENSAJE EN RABBITMQ");

        System.out.println(
            "Exchange: " + RabbitMQConfig.PEDIDOS_EXCHANGE
        );

        System.out.println(
            "Routing Key: " + RabbitMQConfig.PEDIDOS_ROUTING_KEY
        );

        System.out.println("Contenido: " + mensaje);

        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PEDIDOS_EXCHANGE,
            RabbitMQConfig.PEDIDOS_ROUTING_KEY,
            mensaje
        );

        System.out.println("Llamada de publicación realizada");
    }
}
