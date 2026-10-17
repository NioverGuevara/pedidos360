package cl.duoc.rabbitmq_admin_service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/rabbitmq")
public class RabbitMQAdminController {

    private final RabbitMQAdminService adminService;

    // Recursos fundamentales que no se pueden eliminar
    private static final Set<String> RECURSOS_PROTEGIDOS = Set.of(
        "pedidos.queue",
        "notificaciones.queue",
        "tickets.queue",
        "pedidos.dlq",
        "notificaciones.dlq",
        "tickets.dlq",
        "pedidos.exchange",
        "eventos.topic",
        "pedidos.dlx"
    );

    public RabbitMQAdminController(RabbitMQAdminService adminService) {
        this.adminService = adminService;
    }

    // CONSULTAR LAS SEIS COLAS
    @GetMapping("/colas")
    public ResponseEntity<Map<String, Object>> listarColas() {

        Map<String, Object> resultado = new LinkedHashMap<>();

        String[] colas = {
            "pedidos.queue",
            "notificaciones.queue",
            "tickets.queue",
            "pedidos.dlq",
            "notificaciones.dlq",
            "tickets.dlq"
        };

        for (String nombre : colas) {
            resultado.put(
                nombre,
                adminService.obtenerInformacionCola(nombre)
            );
        }

        return ResponseEntity.ok(resultado);
    }

    // CREAR COLA
    @PostMapping("/colas")
    public ResponseEntity<?> crearCola(
            @RequestBody NombreRequest request) {

        String nombre = validarNombre(request.nombre());

        adminService.crearCola(nombre);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Cola creada",
                "nombre", nombre
            ));
    }

    // ELIMINAR COLA
    @DeleteMapping("/colas/{nombre}")
    public ResponseEntity<?> eliminarCola(
            @PathVariable String nombre) {

        nombre = validarNombre(nombre);
        protegerRecursos(nombre);

        boolean eliminada = adminService.eliminarCola(nombre);

        if (!eliminada) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "La cola no existe"));
        }

        return ResponseEntity.ok(
            Map.of(
                "mensaje", "Cola eliminada",
                "nombre", nombre
            )
        );
    }

    // CREAR EXCHANGE
    @PostMapping("/exchanges")
    public ResponseEntity<?> crearExchange(
            @RequestBody NombreRequest request) {

        String nombre = validarNombre(request.nombre());

        adminService.crearExchange(nombre);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Exchange creado",
                "nombre", nombre
            ));
    }

    // ELIMINAR EXCHANGE
    @DeleteMapping("/exchanges/{nombre}")
    public ResponseEntity<?> eliminarExchange(
            @PathVariable String nombre) {

        nombre = validarNombre(nombre);
        protegerRecursos(nombre);

        boolean eliminado = adminService.eliminarExchange(nombre);

        if (!eliminado) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "error",
                    "El exchange no existe"
                ));
        }

        return ResponseEntity.ok(
            Map.of(
                "mensaje", "Exchange eliminado",
                "nombre", nombre
            )
        );
    }

    // CREAR BINDING
    @PostMapping("/bindings")
    public ResponseEntity<?> crearBinding(
            @RequestBody BindingRequest request) {

        String cola = validarNombre(request.cola());
        String exchange = validarNombre(request.exchange());
        String routingKey = validarNombre(request.routingKey());

        adminService.crearBinding(
            cola,
            exchange,
            routingKey
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of(
                "mensaje", "Binding creado"
            ));
    }

    // ELIMINAR BINDING
    @DeleteMapping("/bindings")
    public ResponseEntity<?> eliminarBinding(
            @RequestBody BindingRequest request) {

        String cola = validarNombre(request.cola());
        String exchange = validarNombre(request.exchange());
        String routingKey = validarNombre(request.routingKey());

        protegerRecursos(cola);
        protegerRecursos(exchange);

        adminService.eliminarBinding(
            cola,
            exchange,
            routingKey
        );

        return ResponseEntity.ok(
            Map.of(
                "mensaje", "Binding eliminado"
            )
        );
    }

    // VALIDAR NOMBRES
    private String validarNombre(String nombre) {

        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException(
                "El nombre no puede estar vacio"
            );
        }

        String limpio = nombre.trim();

        if (!limpio.matches("[a-zA-Z0-9._*-]{1,100}")) {
            throw new IllegalArgumentException(
                "El nombre contiene caracteres no permitidos"
            );
        }

        return limpio;
    }

    // PROTEGER RECURSOS PRINCIPALES
    private void protegerRecursos(String nombre) {

        if (RECURSOS_PROTEGIDOS.contains(nombre)) {
            throw new IllegalArgumentException(
                "No se permite eliminar recursos principales de Pedidos360"
            );
        }
    }

    // RESPONDER 400 ANTE DATOS INVALIDOS
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarErrorValidacion(
            IllegalArgumentException error) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error", error.getMessage()
            ));
    }

    // RESPONDER 400 ANTE JSON INVALIDO
    @ExceptionHandler(
        org.springframework.http.converter.HttpMessageNotReadableException.class
    )
    public ResponseEntity<Map<String, String>> manejarJsonInvalido(
            org.springframework.http.converter.HttpMessageNotReadableException error) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(Map.of(
                "error",
                "El cuerpo JSON es obligatorio y debe ser valido"
            ));
    }

    // ESTRUCTURAS DE LAS SOLICITUDES
    public record NombreRequest(String nombre) {}

    public record BindingRequest(
        String cola,
        String exchange,
        String routingKey
    ) {}
}
