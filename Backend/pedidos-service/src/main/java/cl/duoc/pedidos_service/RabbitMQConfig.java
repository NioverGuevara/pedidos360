
package cl.duoc.pedidos_service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Configuración centralizada de pedidos
    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDOS_QUEUE = "pedidos.queue";
    public static final String PEDIDOS_ROUTING_KEY = "pedidos.creado";

    // Configuración centralizada de mensajes rechazados
    public static final String DLX = "pedidos.dlx";
    public static final String DLQ = "pedidos.dlq";
    public static final String DLQ_ROUTING_KEY = "pedidos.error";

    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(PEDIDOS_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    // Cola principal de tipo Quorum con Dead Letter Exchange
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(PEDIDOS_QUEUE)
                .quorum()
                .deadLetterExchange(DLX)
                .deadLetterRoutingKey(DLQ_ROUTING_KEY)
                .build();
    }

    // Cola de errores también de tipo Quorum
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ)
                .quorum()
                .build();
    }

    @Bean
    public Binding pedidosBinding(
            Queue pedidosQueue,
            DirectExchange pedidosExchange) {

        return BindingBuilder
                .bind(pedidosQueue)
                .to(pedidosExchange)
                .with(PEDIDOS_ROUTING_KEY);
    }

    @Bean
    public Binding deadLetterBinding(
            Queue deadLetterQueue,
            DirectExchange deadLetterExchange) {

        return BindingBuilder
                .bind(deadLetterQueue)
                .to(deadLetterExchange)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
