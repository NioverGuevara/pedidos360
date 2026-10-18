
package cl.duoc.pedidos_service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Exchanges
    public static final String PEDIDOS_EXCHANGE = "pedidos.exchange";
    public static final String EVENTOS_TOPIC = "eventos.topic";
    public static final String DLX = "pedidos.dlx";

    // Colas principales
    public static final String PEDIDOS_QUEUE = "pedidos.queue";
    public static final String NOTIFICACIONES_QUEUE = "notificaciones.queue";
    public static final String TICKETS_QUEUE = "tickets.queue";

    // Colas de errores
    public static final String DLQ = "pedidos.dlq";
    public static final String NOTIFICACIONES_DLQ = "notificaciones.dlq";
    public static final String TICKETS_DLQ = "tickets.dlq";

    // Routing keys
    public static final String PEDIDOS_ROUTING_KEY = "pedidos.creado";
    public static final String NOTIFICACIONES_ROUTING_KEY = "notificaciones.*";
    public static final String TICKETS_ROUTING_KEY = "tickets.*";

    // Routing keys para mensajes rechazados
    public static final String DLQ_ROUTING_KEY = "pedidos.error";
    public static final String NOTIFICACIONES_ERROR = "notificaciones.error";
    public static final String TICKETS_ERROR = "tickets.error";

    // Exchanges
    @Bean
    public DirectExchange pedidosExchange() {
        return new DirectExchange(PEDIDOS_EXCHANGE, true, false);
    }

    @Bean
    public TopicExchange eventosTopicExchange() {
        return new TopicExchange(EVENTOS_TOPIC, true, false);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(DLX, true, false);
    }

    // Colas principales Quorum
    @Bean
    public Queue pedidosQueue() {
        return QueueBuilder.durable(PEDIDOS_QUEUE)
                .quorum()
                .build();
    }

    @Bean
    public Queue notificacionesQueue() {
        return QueueBuilder.durable(NOTIFICACIONES_QUEUE)
                .quorum()
                .build();
    }

    @Bean
    public Queue ticketsQueue() {
        return QueueBuilder.durable(TICKETS_QUEUE)
                .quorum()
                .build();
    }

    // Colas de mensajes rechazados
    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(DLQ).quorum().build();
    }

    @Bean
    public Queue notificacionesDlq() {
        return QueueBuilder.durable(NOTIFICACIONES_DLQ).quorum().build();
    }

    @Bean
    public Queue ticketsDlq() {
        return QueueBuilder.durable(TICKETS_DLQ).quorum().build();
    }

    // Bindings principales
    @Bean
    public Binding pedidosBinding(
            @Qualifier("pedidosQueue") Queue queue,
            @Qualifier("pedidosExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(PEDIDOS_ROUTING_KEY);
    }

    @Bean
    public Binding notificacionesBinding(
            @Qualifier("notificacionesQueue") Queue queue,
            @Qualifier("eventosTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(NOTIFICACIONES_ROUTING_KEY);
    }

    @Bean
    public Binding ticketsBinding(
            @Qualifier("ticketsQueue") Queue queue,
            @Qualifier("eventosTopicExchange") TopicExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(TICKETS_ROUTING_KEY);
    }

    // Bindings hacia las DLQ
    @Bean
    public Binding pedidosDlqBinding(
            @Qualifier("deadLetterQueue") Queue queue,
            @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public Binding notificacionesDlqBinding(
            @Qualifier("notificacionesDlq") Queue queue,
            @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(NOTIFICACIONES_ERROR);
    }

    @Bean
    public Binding ticketsDlqBinding(
            @Qualifier("ticketsDlq") Queue queue,
            @Qualifier("deadLetterExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue)
                .to(exchange)
                .with(TICKETS_ERROR);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
