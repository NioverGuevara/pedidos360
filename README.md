# Pedidos360

Pedidos360 es un proyecto desarrollado para la asignatura Desarrollo Cloud Native I. El objetivo es implementar una arquitectura Cloud Native compuesta por frontend, backend, base de datos en la nube y servicios de seguridad.

## Backend

El backend utiliza Java con Spring Boot y actualmente cuenta con el microservicio `pedidos-service`.

Este microservicio permite administrar pedidos mediante una API REST y se encuentra conectado a una base de datos MySQL alojada en AWS RDS.

## Tecnologías utilizadas

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Jakarta Validation
- Maven
- MySQL
- AWS RDS
- Git
- GitHub

## Estructura del microservicio

El microservicio está organizado en las siguientes capas:

- `model`: contiene la entidad Pedido.
- `repository`: acceso a los datos mediante Spring Data JPA.
- `service`: contiene la lógica del microservicio.
- `controller`: contiene los endpoints REST.
- `exception`: manejo de errores y validaciones.

## Base de datos

El proyecto utiliza MySQL en AWS RDS.

Base de datos:

```text
pedidos360
```

La contraseña no se almacena directamente en el código. Se utiliza la variable de entorno:

```text
DB_PASSWORD
```

## Configuración

Antes de ejecutar el backend en PowerShell se debe configurar la contraseña de la base de datos:

```powershell
$env:DB_PASSWORD="CONTRASEÑA_RDS"
```

La contraseña real no debe subirse al repositorio.

## Ejecutar el proyecto

Ingresar al microservicio:

```powershell
cd Backend\pedidos-service
```

Ejecutar:

```powershell
mvn spring-boot:run
```

El servicio queda disponible en:

```text
http://localhost:8080
```

## API de pedidos

La ruta principal es:

```text
/api/pedidos
```

Endpoints disponibles:

| Método | Endpoint | Descripción |
|---|---|---|
| GET | `/api/pedidos` | Listar todos los pedidos |
| GET | `/api/pedidos/{id}` | Buscar pedido por ID |
| POST | `/api/pedidos` | Crear un pedido |
| PUT | `/api/pedidos/{id}` | Actualizar un pedido |
| DELETE | `/api/pedidos/{id}` | Eliminar un pedido |

## Ejemplo de pedido

```json
{
  "cliente": "Alexander Rojas",
  "producto": "Notebook",
  "cantidad": 1,
  "estado": "PENDIENTE"
}
```

## Validaciones

El microservicio valida los datos recibidos antes de almacenarlos.

Por ejemplo:

- El cliente es obligatorio.
- El producto es obligatorio.
- La cantidad debe ser mayor a 0.
- El estado es obligatorio.

Cuando los datos no son válidos, la API responde con código HTTP `400 Bad Request`.

## Pruebas

Las pruebas se pueden ejecutar con:

```powershell
mvn test
```

Actualmente el proyecto incluye pruebas del contexto de Spring Boot y pruebas unitarias del servicio de pedidos utilizando JUnit y Mockito.

## Seguridad

Las credenciales de la base de datos no se almacenan directamente en el repositorio.

La integración de autenticación y autorización mediante JWT forma parte de la integración de seguridad del proyecto.

## Integración Cloud

Actualmente se encuentra implementado:

- Microservicio de pedidos con Spring Boot.
- API REST CRUD.
- Validaciones.
- Persistencia mediante JPA.
- Base de datos MySQL en AWS RDS.
- Pruebas automáticas.
- Control de versiones mediante Git y GitHub.

La integración con EC2, API Gateway, JWT y frontend corresponde a las siguientes etapas de integración del proyecto.