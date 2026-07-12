# FARMAPE Config Server

Microservicio de infraestructura encargado de centralizar la configuracion del backend distribuido de FARMAPE.

## Responsabilidad

El Config Server permite que los demas microservicios obtengan sus propiedades desde una unica fuente. En esta etapa se usa el perfil `native`, por lo que las configuraciones viven dentro de `src/main/resources/configurations`.

## Configuraciones disponibles

| Archivo | Aplicacion |
| --- | --- |
| `application.yaml` | Propiedades comunes para todos los clientes del Config Server. |
| `farmape-ms-eureka.yaml` | Configuracion del servidor Eureka. |
| `farmape-ms-gateway.yaml` | Configuracion del API Gateway. |

## Ejecucion local

Desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-config spring-boot:run
```

## Verificacion

Cuando el servicio este levantado en el puerto `8888`, se pueden consultar las configuraciones asi:

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8888/farmape-ms-gateway/default
http://localhost:8888/actuator/health
```

Estos endpoints permiten validar que la configuracion centralizada esta disponible antes de iniciar Eureka y Gateway.

## Imagen Docker

El modulo incluye un `Dockerfile` basado en Java 21. Antes de construir la imagen se debe generar el `.jar`:

```powershell
..\mvnw.cmd -pl farmape-ms-config clean package -DskipTests
```
