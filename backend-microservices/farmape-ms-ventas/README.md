# FARMAPE Ventas

Microservicio de negocio encargado de ventas y clientes. Persiste sus datos en MongoDB y coordina el stock con `farmape-ms-inventario` mediante OpenFeign y Eureka.

## Responsabilidad

Este servicio atiende el flujo de venta que usa el frontend: consulta y registro de clientes, creacion de ordenes, confirmacion o rechazo de ventas y consulta de ordenes recientes.

Dentro de la arquitectura distribuida:

- Obtiene configuracion desde Config Server.
- Se registra en Eureka.
- Es consumido por el frontend a traves del Gateway.
- Consume Inventario mediante `@FeignClient(name = "farmape-ms-inventario")`.

## Base de datos

Usa MongoDB como motor propio del microservicio.

```text
Base de datos: farmape_ventas
Puerto local Docker: 27017
Puerto interno Kubernetes: 27017
```

El script de inicializacion se encuentra en:

```text
database/ventas/
  01_farmape_ventas_seed.js
```

Colecciones principales:

```text
clientes
ordenes_venta
```

## Endpoints principales

```text
GET   /api/clientes
GET   /api/clientes/documento/{documento}
POST  /api/clientes

POST  /api/ventas
GET   /api/ventas
GET   /api/ventas/ultimas
GET   /api/ventas/{idVenta}
GET   /api/ventas/{idVenta}/detalle
GET   /api/ventas/cliente/{idCliente}
GET   /api/ventas/estado/{estado}
PUT   /api/ventas/{idVenta}
PATCH /api/ventas/{idVenta}/completar
PATCH /api/ventas/{idVenta}/confirmar
PATCH /api/ventas/{idVenta}/cancelar
PATCH /api/ventas/{idVenta}/rechazar
```

Los endpoints `/confirmar`, `/rechazar` y `/ultimas` se mantienen por compatibilidad con el frontend actual.

## Integracion con Inventario

Ventas consulta el producto y registra movimientos de stock mediante OpenFeign:

```java
@FeignClient(name = "farmape-ms-inventario", path = "/api")
```

La resolucion del servicio se realiza por Eureka y Spring Cloud LoadBalancer, igual que en el patron del ejemplo de la profesora.

## Ejemplo de creacion de venta

```json
{
  "idCliente": 1,
  "cliente": "Cliente Prueba",
  "idEmpleado": 1,
  "empleado": "Empleado Prueba",
  "canalPedido": "Presencial",
  "observacion": "Venta de prueba",
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 1
    }
  ]
}
```

## Configuracion

El servicio importa su configuracion desde Config Server mediante:

```yaml
spring:
  application:
    name: farmape-ms-ventas
  config:
    import: ${SPRING_CONFIG_IMPORT:optional:configserver:http://localhost:8888}
```

La configuracion centralizada vive en `farmape-ms-config/src/main/resources/configurations/farmape-ms-ventas.yaml`.

## Ejecucion local

Desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-ventas spring-boot:run
```

Con el entorno completo:

```powershell
docker compose up -d --build
```

## Verificacion

```text
http://localhost:8082/actuator/health
http://localhost:8080/api/ventas/ultimas
http://localhost:8080/api/clientes
```

En Kubernetes se expone internamente como `ventas-service:8082` y el acceso externo debe pasar por el Gateway.
