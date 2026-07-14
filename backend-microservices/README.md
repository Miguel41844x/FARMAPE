# FARMAPE Backend de Microservicios

Backend distribuido para FARMAPE, una aplicacion web orientada a la administracion de una farmacia. El sistema busca soportar procesos de venta multicanal, caja, inventario, compras a proveedores, despacho, recetas magistrales, reportes y auditoria.

Esta carpeta contiene la nueva version basada en microservicios. El backend monolitico anterior se conserva en `backend/` para mantener compatibilidad mientras se migra la funcionalidad por etapas.

## Arquitectura

La solucion sigue una arquitectura de microservicios con servicios de infraestructura y tres servicios de negocio. Los componentes base centralizan configuracion, registran servicios, exponen un unico punto de entrada para el frontend y separan los datos por dominio.

| Modulo | Responsabilidad | Puerto |
| --- | --- | --- |
| `farmape-ms-config` | Centraliza la configuracion de los microservicios con Spring Cloud Config. | `8888` |
| `farmape-ms-eureka` | Registra y permite descubrir microservicios con Eureka Server. | `8761` |
| `farmape-ms-gateway` | Expone el punto de entrada HTTP hacia los microservicios internos. | `8080` |
| `farmape-ms-auth` | Administra autenticacion, usuarios, roles, permisos y trabajadores. | `8083` |
| `farmape-ms-inventario` | Administra productos, categorias, lotes, almacen y despacho. | `8081` |
| `farmape-ms-ventas` | Registra clientes, ventas y coordina stock con Inventario. | `8082` |

## Tecnologias

- Java 21.
- Spring Boot 4.1.0.
- Spring Cloud 2025.1.2.
- Maven multi-modulo.
- Spring Cloud Config Server.
- Netflix Eureka.
- Spring Cloud Gateway WebFlux.
- Spring Data JPA para Auth e Inventario.
- PostgreSQL para Auth.
- MySQL para Inventario.
- Spring Data MongoDB para ventas.
- OpenFeign con descubrimiento Eureka para la comunicacion ventas-inventario.
- Actuator para endpoints de salud.

## Estructura

```text
backend-microservices/
  pom.xml
  database/
    auth/
    inventario/
    ventas/
  docs/
  farmape-ms-config/
  farmape-ms-eureka/
  farmape-ms-gateway/
  farmape-ms-auth/
  farmape-ms-inventario/
  farmape-ms-ventas/
  k8s/
  scripts/
```

## Ejecucion esperada

El orden de arranque local sera:

1. `farmape-ms-config`
2. `farmape-ms-eureka`
3. Bases de datos de negocio
4. `farmape-ms-auth`
5. `farmape-ms-inventario`
6. `farmape-ms-ventas`
7. `farmape-ms-gateway`

Los microservicios de negocio se registran en Eureka y el Gateway enruta las peticiones del frontend hacia ellos.

## Verificacion de configuracion

El Config Server publica las configuraciones centralizadas desde el puerto `8888`.

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8888/farmape-ms-gateway/default
http://localhost:8888/farmape-ms-auth/default
http://localhost:8888/farmape-ms-inventario/default
http://localhost:8888/farmape-ms-ventas/default
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
http://localhost:8080/api/productos/activos
http://localhost:8080/api/ventas/ultimas
http://localhost:8080/api/clientes
```

El Gateway conserva las rutas `/api/...` que consume el frontend y las distribuye hacia Auth, Inventario y Ventas mediante Eureka.

## Empaquetado para contenedores

Cada modulo ejecutable incluye su propio `Dockerfile`. Antes de construir imagenes se deben generar los archivos `.jar`:

```powershell
.\mvnw.cmd clean package -DskipTests
```

Luego se puede construir cada imagen desde su carpeta correspondiente o mediante `docker compose build`.

Cada modulo tambien incluye un `.dockerignore` para enviar al contexto de Docker solo el `.jar` empaquetado y evitar archivos locales, logs, configuraciones privadas o salidas intermedias de Maven.

## Despliegue local con Docker Compose

El archivo `docker-compose.yml` levanta las tres bases de datos de negocio y los servicios en el orden esperado:

1. `postgres-auth`
2. `mysql-inventario`
3. `mongo-ventas`
4. `config-server`
5. `eureka-server`
6. `auth-service`
7. `inventario-service`
8. `ventas-service`
9. `gateway`

La base de datos del microservicio de Auth se inicializa desde `database/auth/`:

- `01_farmape_auth_schema.sql`: crea tablas de roles, permisos, trabajadores, cuentas y solicitudes.
- `02_farmape_auth_data.sql`: carga roles, permisos y datos base de acceso.

La base de datos del microservicio de inventario se inicializa desde `database/inventario/`:

- `01_farmape_inventario_schema.sql`: crea las tablas propias del microservicio.
- `02_farmape_inventario_data.sql`: carga los datos base separados desde el dump original.
- `03_farmape_inventario_verificaciones.sql`: agrega verificaciones de productos recibidos.
- `04_farmape_inventario_despachos.sql`: agrega datos operativos de despacho usados por el frontend.

Los scripts contienen tablas propias de inventario y datos operativos necesarios para que el frontend actual funcione durante la migracion.

La base de datos del microservicio de ventas se inicializa desde `database/ventas/` con scripts `.js` de MongoDB. Docker ejecuta esos scripts solo cuando el volumen `mongo-ventas-data` se crea por primera vez.

Antes de ejecutar Docker Compose, se puede crear un archivo `.env` local a partir del ejemplo:

```powershell
Copy-Item .env.example .env
```

El archivo `.env` permite cambiar puertos, URLs internas y origen permitido del frontend sin modificar archivos versionados.

Auth y Ventas utilizan estos valores por defecto:

```text
AUTH_PORT=8083
AUTH_POSTGRES_PORT=5433
AUTH_DB_NAME=farmape_auth
VENTAS_PORT=8082
VENTAS_MONGO_PORT=27017
VENTAS_MONGO_DATABASE=farmape_ventas
VENTAS_MONGO_URI=mongodb://mongo-ventas:27017/farmape_ventas
```

Los documentos de ventas deben respetar el modelo del backend MySQL: `ordenes_venta` como cabecera y `detalles` embebidos con los campos de `detalle_orden_venta`. El microservicio de ventas tambien expone `/api/clientes` para cubrir la busqueda y registro de clientes que consume el frontend antes de crear la venta.

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
docker compose logs ventas-service --tail=50
docker compose logs gateway --tail=50
```

Plan de verificacion manual:

```powershell
docker compose ps
docker compose logs config-server --tail=50
docker compose logs eureka-server --tail=50
docker compose logs inventario-service --tail=50
docker compose logs ventas-service --tail=50
docker compose logs gateway --tail=50
```

Despues de revisar los logs, comprobar en el navegador o en Insomnia:

```text
http://localhost:8888/farmape-ms-eureka/default
http://localhost:8761
http://localhost:8080/actuator/health
```

## Despliegue local en Kubernetes

La carpeta `k8s/` contiene manifiestos para los servicios de infraestructura, los tres microservicios de negocio y sus bases de datos separadas: PostgreSQL para autenticacion, MySQL para inventario y MongoDB para ventas. Siguiendo el orden de la PPT, primero se construyen los `.jar` e imagenes, luego se crean los ConfigMap con los scripts de inicializacion de datos y finalmente se aplican los manifiestos.

Los `Deployment` mantienen las mismas variables principales del despliegue con Docker Compose: importacion desde Config Server, registro en Eureka, memoria Java, credenciales por `Secret`, volumenes persistentes y origenes permitidos para el frontend.

```powershell
.\mvnw.cmd clean package -DskipTests
docker compose build
kubectl create configmap auth-sql-init --from-file=database/auth --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap inventario-sql-init --from-file=database/inventario --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap ventas-mongo-init --from-file=database/ventas --dry-run=client -o yaml | kubectl apply -f -
kubectl apply -f k8s/
```

Si se despliega con Docker Desktop Kubernetes, las imagenes construidas por `docker compose build` quedan disponibles en el entorno local de Docker. Si se usa Minikube, despues de construir las imagenes se deben cargar dentro del cluster:

```powershell
minikube image load farmape-ms-config
minikube image load farmape-ms-eureka
minikube image load farmape-ms-gateway
minikube image load farmape-ms-auth
minikube image load farmape-ms-inventario
minikube image load farmape-ms-ventas
```

Los ConfigMap `auth-sql-init`, `inventario-sql-init` y `ventas-mongo-init` se montan en `/docker-entrypoint-initdb.d` dentro de PostgreSQL, MySQL y MongoDB. Cada motor ejecuta esos scripts solo en el primer arranque del volumen; si se quiere reinicializar desde cero en un entorno local de pruebas, eliminar antes los PVC `postgres-auth-data`, `mysql-inventario-data` y `mongo-ventas-data`.

El orden de arranque queda controlado con `initContainers`, igual que en el ejemplo de la profesora: Eureka espera a Config Server; los microservicios esperan a Config Server, Eureka y su base; Ventas espera tambien a Inventario porque coordina stock mediante OpenFeign; y el Gateway espera a los tres microservicios de negocio antes de exponerse.

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
kubectl logs deployment/postgres-auth --tail=50
kubectl logs deployment/auth-service --tail=50
kubectl logs deployment/mysql-inventario --tail=50
kubectl logs deployment/inventario-service --tail=50
kubectl logs deployment/mongo-ventas --tail=50
kubectl logs deployment/ventas-service --tail=50
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

Y, si se necesita validar cada servicio de negocio de forma aislada:

```powershell
kubectl port-forward service/auth-service 8083:8083
kubectl port-forward service/ventas-service 8082:8082
```

## Relacion con el frontend

El frontend actual se mantiene sin cambios en sus rutas internas. En desarrollo debe apuntar al Gateway con `VITE_API_URL=http://localhost:8080/api`; en Kubernetes local se puede usar el mismo valor despues de ejecutar `kubectl port-forward service/gateway 8080:8080`.
