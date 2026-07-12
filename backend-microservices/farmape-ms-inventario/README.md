# FARMAPE Inventario

Microservicio de negocio encargado de administrar productos, categorias, lotes y movimientos de almacen de FARMAPE.

## Responsabilidad

Este servicio agrupara las capacidades del monolito relacionadas con:

- Productos y categorias.
- Lotes por producto.
- Movimientos de almacen.
- Consultas de stock para otros microservicios.

## Puerto

```text
8081
```

## Configuracion

El servicio importa su configuracion desde Config Server mediante:

```yaml
spring.config.import=optional:configserver:http://localhost:8888
```

En el despliegue con contenedores se registrara en Eureka para que el Gateway y otros microservicios puedan resolverlo por nombre.
