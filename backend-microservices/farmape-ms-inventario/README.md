# FARMAPE Inventario

Microservicio de negocio encargado de productos, categorias, lotes, movimientos de almacen, verificaciones y despacho operativo.

## Responsabilidad

Este servicio concentra la informacion de stock y las operaciones relacionadas con el almacen. Es una pieza central para FARMAPE porque Ventas consulta productos y registra movimientos de salida mediante OpenFeign.

Sus responsabilidades principales son:

- Mantener el catalogo de productos y categorias.
- Consultar productos activos, stock bajo y detalle de lotes.
- Registrar ingresos, ajustes y movimientos de almacen.
- Gestionar verificaciones de productos recibidos.
- Exponer informacion operativa para entrega en tienda y reparto.

## Base de datos

Usa MySQL como motor propio del microservicio.

```text
Base de datos: farmape_inventario
Puerto local Docker: 3307
Puerto interno Kubernetes: 3306
```

Los scripts de inicializacion se encuentran en:

```text
database/inventario/
  01_farmape_inventario_schema.sql
  02_farmape_inventario_data.sql
  03_farmape_inventario_verificaciones.sql
  04_farmape_inventario_despachos.sql
```

## Endpoints principales

```text
GET   /api/categorias
GET   /api/productos
GET   /api/productos/activos
GET   /api/productos/buscar
GET   /api/productos/stock-bajo
GET   /api/productos/{idProducto}
POST  /api/productos
PUT   /api/productos/{idProducto}
PATCH /api/productos/{idProducto}/estado

GET   /api/inventario/resumen
GET   /api/inventario/productos/{idProducto}/lotes
GET   /api/inventario/movimientos
POST  /api/inventario/movimientos
POST  /api/inventario/ajustes

GET   /api/almacen/ingresos
POST  /api/almacen/ingresos
GET   /api/almacen/verificaciones
PATCH /api/almacen/verificaciones/{idVerificacion}/confirmar
PATCH /api/almacen/verificaciones/{idVerificacion}/observar
GET   /api/almacen/informe

GET   /api/despacho/ordenes-tienda
PATCH /api/despacho/ordenes-tienda/{idOrdenVenta}/entregar
GET   /api/despacho/repartos
POST  /api/despacho/repartos/orden/{idOrdenVenta}
PATCH /api/despacho/repartos/{idReparto}/entregar
```

## Configuracion

El servicio importa su configuracion desde Config Server mediante:

```yaml
spring:
  application:
    name: farmape-ms-inventario
  config:
    import: ${SPRING_CONFIG_IMPORT:optional:configserver:http://localhost:8888}
```

La configuracion centralizada vive en `farmape-ms-config/src/main/resources/configurations/farmape-ms-inventario.yaml`.

## Ejecucion local

Desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-inventario spring-boot:run
```

Con el entorno completo:

```powershell
docker compose up -d --build
```

## Verificacion

```text
http://localhost:8081/actuator/health
http://localhost:8080/api/productos/activos
```

En Kubernetes se expone internamente como `inventario-service:8081` y el acceso externo debe pasar por el Gateway.
