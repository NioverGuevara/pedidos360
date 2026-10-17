
package cl.duoc.pedidos_service;

import com.rabbitmq.client.Channel;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class NotificacionConsumer {

    @RabbitListener(
        queues = RabbitMQConfig.NOTIFICACIONES_QUEUE,
        ackMode = "MANUAL"
    )
    public void recibirNotificacion(Message message, Channel channel)
            throws IOException {

        long deliveryTag = message.getMessageProperties()
                .getDeliveryTag();

        try {
            String contenido = new String(
                    message.getBody(),
                    StandardCharsets.UTF_8
            );

            System.out.println("NOTIFICACION RECIBIDA DESDE RABBITMQ");
            System.out.println("Contenido: " + contenido);

            if (contenido.isBlank()) {
                throw new IllegalArgumentException(
                    "La notificacion no puede estar vacia"
                );
            }

            System.out.println("Notificacion procesada correctamente");

            channel.basicAck(deliveryTag, false);
            System.out.println("ACK enviado para notificacion");

        } catch (Exception error) {

            System.err.println(
                "ERROR EN NOTIFICACION: " + error.getMessage()
            );

            channel.basicNack(deliveryTag, false, false);

            System.err.println(
                "NACK enviado. Mensaje dirigido a notificaciones.dlq"
            );
        }
    }
}
