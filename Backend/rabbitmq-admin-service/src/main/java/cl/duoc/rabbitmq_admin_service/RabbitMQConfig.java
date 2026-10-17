package cl.duoc.rabbitmq_admin_service;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Recursos principales de Pedidos360
    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String PEDIDOS_QUEUE = "pedidos.queue";
    public static final String PEDIDOS_ROUTING_KEY = "pedidos.creado";

    // Recursos de mensajes rechazados
    public static final String DLX = "pedidos.dlx";
    public static final String DLQ = "pedidos.dlq";
    public static final String DLQ_ROUTING_KEY = "pedidos.error";

    // Permite administrar recursos del broker RabbitMQ
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
