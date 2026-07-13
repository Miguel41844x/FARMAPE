# farmape-ms-ventas

Microservicio de ventas de FARMAPE. Persiste sus ordenes de venta en MongoDB y coordina el stock con `farmape-ms-inventario` mediante HTTP.

El documento principal usa la misma base conceptual que el backend SQL:

- Coleccion MongoDB: `ordenes_venta`.
- Cabecera: `idOrdenVenta`, `idCliente`, `cliente`, `idEmpleado`, `empleado`, `canalPedido`, `estado`, `fechaOrden`, `total`, `observacion`.
- Detalles embebidos: `idDetalleVenta`, `idProducto`, `producto`, `cantidad`, `precioUnitario`, `subtotal`.

## Endpoints principales

- `POST /api/ventas`
- `GET /api/ventas`
- `GET /api/ventas/{idVenta}`
- `GET /api/ventas/cliente/{idCliente}`
- `GET /api/ventas/estado/{estado}`
- `PUT /api/ventas/{idVenta}`
- `PATCH /api/ventas/{idVenta}/completar`
- `PATCH /api/ventas/{idVenta}/cancelar`

## Ejemplo de creacion

```json
{
  "idCliente": 1,
  "cliente": "Cliente Prueba",
  "idEmpleado": 1,
  "empleado": "Empleado Prueba",
  "canalPedido": "Presencial",
  "observacion": "Venta de prueba",
  "detalles": [
    {
      "idProducto": 1,
      "cantidad": 1
    }
  ],
  "descuento": 0,
  "metodoPago": "EFECTIVO"
}
```

## Datos iniciales en MongoDB

Ventas no incluye migracion JDBC. Si necesitas datos iniciales, crea scripts `.js` en `backend-microservices/database/ventas/`. Docker Compose monta esa carpeta en `/docker-entrypoint-initdb.d`, igual que inventario monta sus scripts SQL.

Ejemplo de documento:

```javascript
db = db.getSiblingDB("farmape_ventas");

db.ordenes_venta.insertMany([
  {
    idOrdenVenta: 1,
    idCliente: 1,
    cliente: "Cliente Prueba",
    idEmpleado: 1,
    empleado: "Empleado Prueba",
    canalPedido: "Presencial",
    estado: "Pendiente",
    fechaOrden: ISODate("2026-07-13T00:00:00Z"),
    total: NumberDecimal("3.00"),
    observacion: "Venta de prueba",
    detalles: [
      {
        idDetalleVenta: 1,
        idProducto: 1,
        producto: "Paracetamol",
        cantidad: 2,
        precioUnitario: NumberDecimal("1.50"),
        subtotal: NumberDecimal("3.00")
      }
    ]
  }
]);
```
