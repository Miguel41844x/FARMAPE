# FARMAPE Auth

Microservicio de negocio encargado de autenticacion, cuentas de usuario, trabajadores, roles y permisos de FARMAPE.

## Responsabilidad

Este servicio concentra la administracion de acceso al sistema. Su objetivo es separar del resto de microservicios todo lo relacionado con identidad de usuarios, validacion de credenciales y autorizacion por permisos.

Dentro de la arquitectura de microservicios se registra en Eureka, obtiene su configuracion desde Config Server y es consumido desde el frontend a traves del API Gateway.

## Base de datos

Usa PostgreSQL como motor propio del microservicio.

```text
Base de datos: farmape_auth
Puerto local Docker: 5433
Puerto interno Kubernetes: 5432
```

Los scripts de inicializacion se encuentran en:

```text
database/auth/
  01_farmape_auth_schema.sql
  02_farmape_auth_data.sql
```

## Endpoints principales

```text
POST   /api/auth/login
POST   /api/auth/refresh
POST   /api/auth/solicitar-restablecimiento
GET    /api/auth/solicitudes-restablecimiento
GET    /api/usuarios
POST   /api/usuarios
PUT    /api/usuarios/{id}
PATCH  /api/usuarios/{id}/clave
PATCH  /api/usuarios/{id}/estado
GET    /api/trabajadores
POST   /api/trabajadores
PUT    /api/trabajadores/{id}
PATCH  /api/trabajadores/{id}/estado
GET    /api/roles
POST   /api/roles
PUT    /api/roles/{idRol}
PUT    /api/roles/{idRol}/permisos
PATCH  /api/roles/{idRol}/estado
DELETE /api/roles/{idRol}
GET    /api/permisos
GET    /api/perfil
PUT    /api/perfil
```

## Configuracion

El archivo local `src/main/resources/application.yaml` define el nombre del servicio y la importacion del Config Server:

```yaml
spring:
  application:
    name: farmape-ms-auth
  config:
    import: ${SPRING_CONFIG_IMPORT:optional:configserver:http://localhost:8888}
```

La configuracion centralizada vive en `farmape-ms-config/src/main/resources/configurations/farmape-ms-auth.yaml`.

## Ejecucion local

Desde la carpeta `backend-microservices`:

```powershell
.\mvnw.cmd -pl farmape-ms-auth spring-boot:run
```

Para levantarlo con todo el ecosistema recomendado por la PPT:

```powershell
docker compose up -d --build
```

## Verificacion

```text
http://localhost:8083/actuator/health
http://localhost:8080/api/auth/login
```

En Kubernetes se expone internamente como `auth-service:8083` y el acceso externo debe pasar por el Gateway.
