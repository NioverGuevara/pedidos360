
package cl.duoc.pedidos_service;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class TicketConsumer {

    @RabbitListener(
        queues = RabbitMQConfig.TICKETS_QUEUE,
        ackMode = "MANUAL"
    )
    public void recibirTicket(Message message, Channel channel)
            throws IOException {

        long deliveryTag = message.getMessageProperties()
                .getDeliveryTag();

        try {
            String contenido = new String(
                    message.getBody(),
                    StandardCharsets.UTF_8
            );

            System.out.println("TICKET RECIBIDO DESDE RABBITMQ");
            System.out.println("Contenido: " + contenido);

            if (contenido.isBlank()) {
                throw new IllegalArgumentException(
                    "El ticket no puede estar vacio"
                );
            }

            System.out.println("Ticket procesado correctamente");

            channel.basicAck(deliveryTag, false);
            System.out.println("ACK enviado para ticket");

        } catch (Exception error) {

            System.err.println(
                "ERROR EN TICKET: " + error.getMessage()
            );

            channel.basicNack(deliveryTag, false, false);

            System.err.println(
                "NACK enviado. Mensaje dirigido a tickets.dlq"
            );
        }
    }
}
