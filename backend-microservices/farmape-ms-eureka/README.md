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

Cuando existan microservicios de negocio y el Gateway este activo, deberan aparecer registrados en el panel de Eureka.
