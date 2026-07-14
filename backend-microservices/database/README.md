# FARMAPE Bases de Datos por Microservicio

Esta carpeta contiene los scripts de inicializacion de las bases de datos usadas por los microservicios de negocio. La separacion responde al principio de autonomia de datos en una arquitectura de microservicios: cada servicio administra su propio modelo y su propio motor.

## Distribucion

| Carpeta | Microservicio | Motor | Base de datos |
| --- | --- | --- | --- |
| `auth/` | `farmape-ms-auth` | PostgreSQL | `farmape_auth` |
| `inventario/` | `farmape-ms-inventario` | MySQL | `farmape_inventario` |
| `ventas/` | `farmape-ms-ventas` | MongoDB | `farmape_ventas` |

## Auth

```text
auth/
  01_farmape_auth_schema.sql
  02_farmape_auth_data.sql
```

Contiene las tablas y datos base para roles, permisos, trabajadores, cuentas de usuario y solicitudes de restablecimiento de clave.

## Inventario

```text
inventario/
  01_farmape_inventario_schema.sql
  02_farmape_inventario_data.sql
  03_farmape_inventario_verificaciones.sql
  04_farmape_inventario_despachos.sql
```

Contiene productos, categorias, lotes, movimientos de almacen, verificaciones y datos operativos de despacho necesarios para el frontend actual.

## Ventas

```text
ventas/
  01_farmape_ventas_seed.js
```

Inicializa colecciones MongoDB para clientes y ordenes de venta. El modelo usa documentos para representar la cabecera de la venta y sus detalles embebidos.

## Uso con Docker Compose

Docker Compose monta cada carpeta en `/docker-entrypoint-initdb.d` del contenedor correspondiente. Los scripts se ejecutan automaticamente solo cuando el volumen de la base de datos se crea por primera vez.

## Uso con Kubernetes

Antes de aplicar los manifiestos se crean ConfigMap con estos scripts:

```powershell
kubectl create configmap auth-sql-init --from-file=database/auth --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap inventario-sql-init --from-file=database/inventario --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap ventas-mongo-init --from-file=database/ventas --dry-run=client -o yaml | kubectl apply -f -
```

Si se necesita reinicializar datos en un entorno local de pruebas, primero se deben eliminar los PVC asociados para que el motor vuelva a ejecutar los scripts de inicializacion.
