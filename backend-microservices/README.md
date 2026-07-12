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

## Verificacion de configuracion

El Config Server publica las configuraciones centralizadas desde el puerto `8888`.

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8888/farmape-ms-gateway/default
```

Estas URLs deben responder antes de levantar los demas servicios de infraestructura.

## Verificacion de Eureka

Eureka Server publica su panel de registro de servicios en:

```text
http://localhost:8761
```

En desarrollo local se ejecuta como servidor unico, por eso no se registra a si mismo ni descarga registros de otros servidores Eureka.

## Verificacion del Gateway

El API Gateway se publica en el puerto `8080`:

```text
http://localhost:8080/actuator/health
```

Por ahora usa descubrimiento dinamico de servicios. Cuando se creen los microservicios de negocio, se agregaran rutas explicitas para conservar las rutas `/api/...` que consume el frontend.

## Empaquetado para contenedores

Cada microservicio de infraestructura incluye su propio `Dockerfile`. Antes de construir imagenes se deben generar los archivos `.jar`:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Luego se podra construir cada imagen desde su carpeta correspondiente. El despliegue coordinado se definira con `docker-compose.yml`.

Cada modulo tambien incluye un `.dockerignore` para enviar al contexto de Docker solo el `.jar` empaquetado y evitar archivos locales, logs, configuraciones privadas o salidas intermedias de Maven.

## Despliegue local con Docker Compose

El archivo `docker-compose.yml` levanta los servicios de infraestructura en el orden esperado:

1. `config-server`
2. `eureka-server`
3. `gateway`

Antes de ejecutar Docker Compose, se puede crear un archivo `.env` local a partir del ejemplo:

```powershell
Copy-Item .env.example .env
```

El archivo `.env` permite cambiar puertos, URLs internas y origen permitido del frontend sin modificar archivos versionados.

Para ejecutar el entorno local:

```powershell
.\mvnw.cmd clean package -DskipTests
docker compose up -d --build
```

Para apagar los contenedores:

```powershell
docker compose down
```

Verificacion rapida:

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8761
http://localhost:8080/actuator/health
```

## Relacion con el frontend

El frontend actual se mantiene sin cambios. Mas adelante, cuando el Gateway tenga las rutas de negocio, el frontend debera apuntar a `http://localhost:8080` en desarrollo o a la URL publica del Gateway en despliegue.
