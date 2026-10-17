
package cl.duoc.pedidos_service;

import com.rabbitmq.client.Channel;
import java.io.IOException;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PedidoConsumer {

    @RabbitListener(
        queues = RabbitMQConfig.PEDIDOS_QUEUE,
        ackMode = "MANUAL"
    )
    public void recibirMensaje(Message message, Channel channel)
            throws IOException {

        long deliveryTag = message.getMessageProperties()
                                  .getDeliveryTag();

        try {
            String contenido = new String(
                message.getBody(),
                java.nio.charset.StandardCharsets.UTF_8
            );

            System.out.println("MENSAJE RECIBIDO DESDE RABBITMQ");
            System.out.println("Contenido: " + contenido);

            if (!contenido.startsWith("Pedido creado con ID: ")) {
                throw new IllegalArgumentException(
                    "Formato de mensaje no válido"
                );
            }

            System.out.println("Pedido procesado correctamente");

            channel.basicAck(deliveryTag, false);

            System.out.println("ACK enviado a RabbitMQ");

        } catch (Exception error) {

            System.err.println(
                "ERROR PROCESANDO MENSAJE: " + error.getMessage()
            );

            // Rechazar sin reencolar: RabbitMQ lo enviará
            // a la DLQ configurada en la cola principal.
            channel.basicNack(deliveryTag, false, false);

            System.err.println("NACK enviado a RabbitMQ");
        }
    }
}
