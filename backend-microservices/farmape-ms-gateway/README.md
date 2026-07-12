# FARMAPE API Gateway

Microservicio de infraestructura que funciona como punto de entrada HTTP para el backend distribuido de FARMAPE.

## Responsabilidad

El Gateway recibe las peticiones del frontend y las enruta hacia los microservicios internos registrados en Eureka. En esta primera etapa aun no existen microservicios de negocio, por lo que se deja preparado el descubrimiento dinamico de servicios.

## Configuracion

El Gateway obtiene su configuracion desde Config Server mediante:

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

Tambien tiene valores locales de respaldo en `application.yaml` para facilitar el desarrollo.

## Caracteristicas iniciales

- Puerto local `8080`.
- Cliente de Config Server.
- Cliente de Eureka para consultar servicios registrados.
- Discovery locator activo para rutas basadas en servicios registrados.
- Filtro `X-Gateway-Request` para identificar peticiones procesadas por el Gateway.
- CORS preparado para el frontend local en `http://localhost:5173`.

## Ejecucion local

Primero se deben levantar `farmape-ms-config` y `farmape-ms-eureka`. Luego, desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-gateway spring-boot:run
```

## Verificacion

Con el Gateway levantado en el puerto `8080`, se puede verificar desde:

```text
http://localhost:8080/actuator/health
```

Cuando se agreguen microservicios de negocio, se definiran rutas explicitas para mantener estable el contrato HTTP consumido por el frontend.
