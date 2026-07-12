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
| `farmape-ms-inventario` | Administra productos, categorias, lotes y movimientos de almacen. | `8081` |

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
  database/
    inventario/
  farmape-ms-config/
  farmape-ms-eureka/
  farmape-ms-gateway/
  farmape-ms-inventario/
```

## Ejecucion esperada

El orden de arranque local sera:

1. `farmape-ms-config`
2. `farmape-ms-eureka`
3. `farmape-ms-gateway`
4. `farmape-ms-inventario`

Los microservicios de negocio se registran en Eureka y el Gateway enruta las peticiones del frontend hacia ellos.

## Verificacion de configuracion

El Config Server publica las configuraciones centralizadas desde el puerto `8888`.

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8888/farmape-ms-gateway/default
http://localhost:8888/farmape-ms-inventario/default
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

El archivo `docker-compose.yml` levanta la base de datos de inventario y los servicios en el orden esperado:

1. `mysql-inventario`
2. `config-server`
3. `eureka-server`
4. `inventario-service`
5. `gateway`

La base de datos del microservicio de inventario se inicializa desde `database/inventario/`:

- `01_farmape_inventario_schema.sql`: crea las tablas propias del microservicio.
- `02_farmape_inventario_data.sql`: carga los datos base separados desde el dump original.
- `03_farmape_inventario_verificaciones.sql`: agrega verificaciones de productos recibidos.
- `04_farmape_inventario_despachos.sql`: agrega datos operativos de despacho usados por el frontend.

Los scripts contienen tablas propias de inventario y datos operativos necesarios para que el frontend actual funcione durante la migracion.

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
http://localhost:8080/actuator/health/readiness
```

El archivo Compose usa `depends_on` con `service_healthy` para que Inventario espere a MySQL, Config Server y Eureka; luego el Gateway espera a los servicios necesarios antes de exponerse.

Comandos utiles de revision:

```powershell
docker compose ps
docker compose logs config-server --tail=50
docker compose logs eureka-server --tail=50
docker compose logs inventario-service --tail=50
docker compose logs gateway --tail=50
```

Plan de verificacion manual:

```powershell
docker compose ps
docker compose logs config-server --tail=50
docker compose logs eureka-server --tail=50
docker compose logs inventario-service --tail=50
docker compose logs gateway --tail=50
```

Despues de revisar los logs, comprobar en el navegador o en Insomnia:

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8761
http://localhost:8080/actuator/health
```

## Despliegue local en Kubernetes

La carpeta `k8s/` contiene manifiestos base para los servicios de infraestructura, el microservicio de inventario y su base de datos MySQL separada. Siguiendo el orden de la PPT, primero se construyen los `.jar` e imagenes, luego se crea el ConfigMap con los SQL de inventario y finalmente se aplican los manifiestos.

Los `Deployment` mantienen las mismas variables principales del despliegue con Docker Compose: importacion desde Config Server, registro en Eureka, memoria Java y origenes permitidos para el frontend.

```powershell
.\mvnw.cmd clean package -DskipTests
docker compose build
kubectl create configmap inventario-sql-init --from-file=database/inventario --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f k8s/
```

El ConfigMap `inventario-sql-init` se monta en `/docker-entrypoint-initdb.d` del pod `mysql-inventario`. MySQL ejecuta esos scripts solo en el primer arranque del volumen; si se quiere reinicializar desde cero en un entorno local de pruebas, eliminar antes el PVC `mysql-inventario-data`.

Si se desea regenerar manifiestos con Kompose, usar la variante compatible que evita `depends_on.condition`:

```powershell
kompose convert -f docker-compose.kompose.yml -o k8s/
```

Verificacion en Kubernetes:

```powershell
kubectl get pods
kubectl get svc
kubectl logs deployment/config-server --tail=50
kubectl logs deployment/eureka-server --tail=50
kubectl logs deployment/mysql-inventario --tail=50
kubectl logs deployment/inventario-service --tail=50
kubectl logs deployment/gateway --tail=50
```

Para consumir el Gateway desde la maquina local:

```powershell
kubectl port-forward service/gateway 8080:8080
```

Tambien se puede revisar directamente el microservicio de inventario:

```powershell
kubectl port-forward service/inventario-service 8081:8081
```

## Relacion con el frontend

El frontend actual se mantiene sin cambios. Mas adelante, cuando el Gateway tenga las rutas de negocio, el frontend debera apuntar a `http://localhost:8080` en desarrollo o a la URL publica del Gateway en despliegue.
