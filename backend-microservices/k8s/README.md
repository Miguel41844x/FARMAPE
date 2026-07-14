# FARMAPE Kubernetes

Esta carpeta contiene los manifiestos Kubernetes para desplegar localmente la arquitectura de microservicios de FARMAPE, siguiendo el enfoque presentado en la PPT y el ejemplo de la profesora: servicios de infraestructura, microservicios de negocio, bases de datos separadas, `initContainers`, health checks y servicios internos.

## Componentes desplegados

| Componente | Tipo | Puerto |
| --- | --- | --- |
| `config-server` | Spring Cloud Config Server | `8888` |
| `eureka-server` | Eureka Server | `8761` |
| `gateway` | Spring Cloud Gateway | `8080` |
| `auth-service` | Microservicio de autenticacion | `8083` |
| `inventario-service` | Microservicio de inventario | `8081` |
| `ventas-service` | Microservicio de ventas | `8082` |
| `postgres-auth` | Base de datos PostgreSQL para Auth | `5432` |
| `mysql-inventario` | Base de datos MySQL para Inventario | `3306` |
| `mongo-ventas` | Base de datos MongoDB para Ventas | `27017` |

## Archivos principales

```text
config-server-*.yaml
eureka-server-*.yaml
gateway-*.yaml
auth-service-*.yaml
inventario-service-*.yaml
ventas-service-*.yaml
postgres-auth-*.yaml
mysql-inventario-*.yaml
mongo-ventas-*.yaml
```

Los archivos `*-deployment.yaml` definen `Deployment` y, cuando corresponde, `PersistentVolumeClaim`. Los archivos `*-service.yaml` definen servicios internos de Kubernetes. Los archivos `*-secret.yaml` contienen credenciales de desarrollo para las bases locales.

## Orden de arranque

El orden esperado es:

1. Bases de datos.
2. Config Server.
3. Eureka Server.
4. Microservicios de negocio.
5. API Gateway.

Los `initContainers` ayudan a respetar ese orden dentro del cluster. Por ejemplo, Ventas espera a MongoDB, Config Server, Eureka e Inventario porque necesita consultar productos y registrar movimientos de stock.

## ConfigMap requeridos

Antes de aplicar los manifiestos se deben crear los ConfigMap de inicializacion:

```powershell
kubectl create configmap auth-sql-init --from-file=database/auth --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap inventario-sql-init --from-file=database/inventario --dry-run=client -o yaml | kubectl apply -f -
kubectl create configmap ventas-mongo-init --from-file=database/ventas --dry-run=client -o yaml | kubectl apply -f -
```

Luego se aplican los manifiestos:

```powershell
kubectl apply -f k8s/
```

## Imagenes locales

Con Docker Desktop Kubernetes, las imagenes construidas por Docker suelen estar disponibles para el cluster local. Con Minikube se deben cargar explicitamente:

```powershell
minikube image load farmape-ms-config
minikube image load farmape-ms-eureka
minikube image load farmape-ms-gateway
minikube image load farmape-ms-auth
minikube image load farmape-ms-inventario
minikube image load farmape-ms-ventas
```

## Verificacion

```powershell
kubectl get pods
kubectl get svc
kubectl logs deployment/config-server --tail=50
kubectl logs deployment/eureka-server --tail=50
kubectl logs deployment/auth-service --tail=50
kubectl logs deployment/inventario-service --tail=50
kubectl logs deployment/ventas-service --tail=50
kubectl logs deployment/gateway --tail=50
```

Para consumir el backend desde el frontend local:

```powershell
kubectl port-forward service/gateway 8080:8080
```

El frontend debe usar:

```text
VITE_API_URL=http://localhost:8080/api
```
