# FARMAPE Backend de Microservicios

Backend distribuido para FARMAPE, una aplicacion web orientada a la administracion de una farmacia. El sistema busca soportar procesos de venta multicanal, caja, inventario, compras a proveedores, despacho, recetas magistrales, reportes y auditoria.

Esta carpeta contiene la nueva version basada en microservicios. El backend monolitico anterior se conserva en `backend/` para mantener compatibilidad mientras se migra la funcionalidad por etapas.

## Arquitectura

La solucion sigue una arquitectura de microservicios con servicios de infraestructura y, posteriormente, servicios de negocio. La primera etapa implementa los componentes base que permiten centralizar configuracion, registrar servicios y exponer un unico punto de entrada para el frontend.

| Modulo | Responsabilidad | Puerto |
| --- | --- | --- |
| `farmape-ms-config` | Centraliza la configuracion de los microservicios con Spring Cloud Config. | `8888` |
| `farmape-ms-eureka` | Registra y permite descubrir microservicios con Eureka Server. | `8761` |
| `farmape-ms-gateway` | Expone el punto de entrada HTTP hacia los microservicios internos. | `8080` |

## Tecnologias

- Java 21.
- Spring Boot 4.1.0.
- Spring Cloud 2025.1.2.
- Maven multi-modulo.
- Spring Cloud Config Server.
- Netflix Eureka.
- Spring Cloud Gateway WebFlux.
- Actuator para endpoints de salud.

## Estructura

```text
backend-microservices/
  pom.xml
  farmape-ms-config/
  farmape-ms-eureka/
  farmape-ms-gateway/
```

## Ejecucion esperada

El orden de arranque local sera:

1. `farmape-ms-config`
2. `farmape-ms-eureka`
3. `farmape-ms-gateway`

Cuando se agreguen microservicios de negocio, estos se registraran en Eureka y el Gateway enroutara las peticiones del frontend hacia ellos.

## Relacion con el frontend

El frontend actual se mantiene sin cambios. Mas adelante, cuando el Gateway tenga las rutas de negocio, el frontend debera apuntar a `http://localhost:8080` en desarrollo o a la URL publica del Gateway en despliegue.
