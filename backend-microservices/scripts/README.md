# FARMAPE Scripts

Esta carpeta queda reservada para scripts auxiliares del backend de microservicios.

Actualmente el proyecto se levanta con comandos directos de Maven, Docker Compose y Kubernetes, por lo que no se requiere ningun script adicional para ejecutar la aplicacion.

## Uso recomendado

Agregar aqui solo scripts que automaticen tareas repetitivas del proyecto, por ejemplo:

```text
scripts/
  build-images.ps1
  load-minikube-images.ps1
  verify-health.ps1
```

Los scripts deben ser auxiliares y no reemplazar la documentacion principal de ejecucion, para que el proyecto siga siendo entendible para todo el equipo.
