# FARMAPE API Gateway

Microservicio de infraestructura que funciona como punto de entrada HTTP para el backend distribuido de FARMAPE.

## Responsabilidad

El Gateway recibe las peticiones del frontend y las enruta hacia los microservicios internos registrados en Eureka. Su funcion es mantener un unico punto de entrada HTTP para que el frontend no dependa de los puertos internos de cada microservicio.

## Configuracion

El Gateway obtiene su configuracion desde Config Server mediante:

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

Tambien tiene valores locales de respaldo en `application.yaml` para facilitar el desarrollo.

## Caracteristicas principales

- Puerto local `8080`.
- Cliente de Config Server.
- Cliente de Eureka para consultar servicios registrados.
- Rutas explicitas hacia Auth, Inventario y Ventas.
- Discovery locator activo para rutas basadas en servicios registrados.
- Filtro `X-Gateway-Request` para identificar peticiones procesadas por el Gateway.
- CORS preparado para el frontend local en `http://localhost:5173`.

## Rutas de negocio

| Rutas del frontend | Microservicio destino |
| --- | --- |
| `/api/auth/**` | `farmape-ms-auth` |
| `/api/usuarios/**` | `farmape-ms-auth` |
| `/api/trabajadores/**` | `farmape-ms-auth` |
| `/api/roles/**` | `farmape-ms-auth` |
| `/api/permisos/**` | `farmape-ms-auth` |
| `/api/perfil/**` | `farmape-ms-auth` |
| `/api/productos/**` | `farmape-ms-inventario` |
| `/api/categorias/**` | `farmape-ms-inventario` |
| `/api/almacen/**` | `farmape-ms-inventario` |
| `/api/despacho/**` | `farmape-ms-inventario` |
| `/api/ventas/**` | `farmape-ms-ventas` |
| `/api/clientes/**` | `farmape-ms-ventas` |

## Ejecucion local

Primero se deben levantar `farmape-ms-config`, `farmape-ms-eureka` y los microservicios de negocio. Luego, desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-gateway spring-boot:run
```

## Verificacion

Con el Gateway levantado en el puerto `8080`, se puede verificar desde:

```text
http://localhost:8080/actuator/health
http://localhost:8080/api/productos/activos
```

El frontend debe apuntar a `http://localhost:8080/api` mediante `VITE_API_URL` para consumir esta infraestructura.

## Imagen Docker

El modulo incluye un `Dockerfile` basado en Java 21. Antes de construir la imagen se debe generar el `.jar`:

```powershell
..\mvnw.cmd -pl farmape-ms-gateway clean package -DskipTests
```
