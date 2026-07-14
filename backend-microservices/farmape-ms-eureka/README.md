# FARMAPE Eureka Server

Microservicio de infraestructura encargado del registro y descubrimiento de servicios del backend distribuido de FARMAPE.

## Responsabilidad

Eureka Server mantiene un registro de los microservicios disponibles para que otros componentes, como el API Gateway, puedan localizarlos sin depender de direcciones fijas.

En esta etapa, Eureka no se registra a si mismo y no descarga el registro de otros servidores Eureka, porque se ejecuta como servidor unico para desarrollo local.

## Configuracion

La configuracion principal se obtiene desde Config Server mediante:

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8888
```

El archivo local `application.yaml` tambien contiene valores base para permitir el arranque durante desarrollo si el Config Server aun no esta disponible.

## Ejecucion local

Primero se debe levantar `farmape-ms-config`. Luego, desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-eureka spring-boot:run
```

## Verificacion

Con Eureka levantado en el puerto `8761`, se puede verificar desde:

```text
http://localhost:8761
http://localhost:8761/actuator/health
```

Cuando el entorno completo esta levantado, en el panel deben aparecer registrados los microservicios de negocio y el Gateway.

## Servicios esperados

```text
farmape-ms-auth
farmape-ms-inventario
farmape-ms-ventas
farmape-ms-gateway
```

Eureka permite que el Gateway enrute por `lb://NOMBRE-DEL-SERVICIO` y que Ventas resuelva Inventario mediante OpenFeign, siguiendo el patron usado en el ejemplo de la profesora.

## Imagen Docker

El modulo incluye un `Dockerfile` basado en Java 21. Antes de construir la imagen se debe generar el `.jar`:

```powershell
..\mvnw.cmd -pl farmape-ms-eureka clean package -DskipTests
```
