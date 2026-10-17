package cl.duoc.rabbitmq_admin_service;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class RabbitMQAdminService {

    private final RabbitAdmin rabbitAdmin;

    public RabbitMQAdminService(RabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    private String validarNombre(String nombre, String tipo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(tipo + " no puede estar vacío");
        }
        return nombre;
    }

    // CONSULTAR INFORMACION DE UNA COLA
    public Map<String, Object> obtenerInformacionCola(String nombre) {
        nombre = validarNombre(nombre, "El nombre de la cola");

        Map<String, Object> informacion = new LinkedHashMap<>();

        Properties propiedades = rabbitAdmin.getQueueProperties(nombre);

        informacion.put("nombre", nombre);
        informacion.put("existe", propiedades != null);

        if (propiedades != null) {

            informacion.put(
                "mensajes",
                propiedades.get(RabbitAdmin.QUEUE_MESSAGE_COUNT)
            );

            informacion.put(
                "consumidores",
                propiedades.get(RabbitAdmin.QUEUE_CONSUMER_COUNT)
            );

        } else {

            informacion.put("mensajes", 0);
            informacion.put("consumidores", 0);
        }

        return informacion;
    }

    // CREAR COLA
    public void crearCola(String nombre) {

        Queue cola = new Queue(nombre, true);

        rabbitAdmin.declareQueue(cola);
    }

    // ELIMINAR COLA
    public boolean eliminarCola(String nombre) {

        return rabbitAdmin.deleteQueue(nombre);
    }

    // CREAR EXCHANGE
    public void crearExchange(String nombre) {

        DirectExchange exchange = new DirectExchange(
            nombre,
            true,
            false
        );

        rabbitAdmin.declareExchange(exchange);
    }

    // ELIMINAR EXCHANGE
    public boolean eliminarExchange(String nombre) {

        return rabbitAdmin.deleteExchange(nombre);
    }

    // CREAR BINDING
    public void crearBinding(
            String nombreCola,
            String nombreExchange,
            String routingKey) {

        Queue cola = new Queue(nombreCola, true);

        DirectExchange exchange = new DirectExchange(
            nombreExchange,
            true,
            false
        );

        Binding binding = BindingBuilder
                .bind(cola)
                .to(exchange)
                .with(routingKey);

        rabbitAdmin.declareBinding(binding);
    }

    // ELIMINAR BINDING
    public void eliminarBinding(
            String nombreCola,
            String nombreExchange,
            String routingKey) {

        Binding binding = new Binding(
            nombreCola,
            Binding.DestinationType.QUEUE,
            nombreExchange,
            routingKey,
            null
        );

        rabbitAdmin.removeBinding(binding);
    }
}
