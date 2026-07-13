db = db.getSiblingDB("farmape_ventas");

db.ordenes_venta.createIndex({ idOrdenVenta: 1 }, { unique: true });
db.ordenes_venta.createIndex({ idCliente: 1 });
db.ordenes_venta.createIndex({ estado: 1 });
db.ordenes_venta.createIndex({ fechaOrden: -1 });

db.ordenes_venta.bulkWrite([
  {
    updateOne: {
      filter: { idOrdenVenta: 1 },
      update: {
        $set: {
          idOrdenVenta: 1,
          idCliente: 1,
          cliente: "Cliente Mostrador",
          idEmpleado: 1,
          empleado: "Empleado Prueba",
          canalPedido: "Presencial",
          estado: "Pendiente",
          fechaOrden: ISODate("2026-07-01T09:15:00Z"),
          total: NumberDecimal("3.80"),
          observacion: "Venta simulada desde datos de productos MySQL",
          detalles: [
            {
              idDetalleVenta: 1,
              idProducto: 1,
              producto: "Paracetamol 500mg",
              cantidad: 2,
              precioUnitario: NumberDecimal("1.00"),
              subtotal: NumberDecimal("2.00")
            },
            {
              idDetalleVenta: 2,
              idProducto: 2,
              producto: "Ibuprofeno 400mg",
              cantidad: 1,
              precioUnitario: NumberDecimal("1.80"),
              subtotal: NumberDecimal("1.80")
            }
          ]
        }
      },
      upsert: true
    }
  },
  {
    updateOne: {
      filter: { idOrdenVenta: 2 },
      update: {
        $set: {
          idOrdenVenta: 2,
          idCliente: 2,
          cliente: "Ana Perez",
          idEmpleado: 1,
          empleado: "Empleado Prueba",
          canalPedido: "WhatsApp",
          estado: "Confirmada",
          fechaOrden: ISODate("2026-07-02T14:40:00Z"),
          total: NumberDecimal("5.20"),
          observacion: "Pedido confirmado por WhatsApp",
          detalles: [
            {
              idDetalleVenta: 1,
              idProducto: 5,
              producto: "Amoxicilina 500mg",
              cantidad: 1,
              precioUnitario: NumberDecimal("2.80"),
              subtotal: NumberDecimal("2.80")
            },
            {
              idDetalleVenta: 2,
              idProducto: 8,
              producto: "Loratadina 10mg",
              cantidad: 2,
              precioUnitario: NumberDecimal("1.20"),
              subtotal: NumberDecimal("2.40")
            }
          ]
        }
      },
      upsert: true
    }
  },
  {
    updateOne: {
      filter: { idOrdenVenta: 3 },
      update: {
        $set: {
          idOrdenVenta: 3,
          idCliente: 3,
          cliente: "Luis Torres",
          idEmpleado: 2,
          empleado: "Cajero Prueba",
          canalPedido: "Telefono",
          estado: "Pagada",
          fechaOrden: ISODate("2026-07-03T16:05:00Z"),
          total: NumberDecimal("6.60"),
          observacion: "Venta telefonica pagada",
          detalles: [
            {
              idDetalleVenta: 1,
              idProducto: 12,
              producto: "Omeprazol 20mg",
              cantidad: 3,
              precioUnitario: NumberDecimal("1.20"),
              subtotal: NumberDecimal("3.60")
            },
            {
              idDetalleVenta: 2,
              idProducto: 16,
              producto: "Vitamina C 1g",
              cantidad: 2,
              precioUnitario: NumberDecimal("1.50"),
              subtotal: NumberDecimal("3.00")
            }
          ]
        }
      },
      upsert: true
    }
  },
  {
    updateOne: {
      filter: { idOrdenVenta: 4 },
      update: {
        $set: {
          idOrdenVenta: 4,
          idCliente: 4,
          cliente: "Maria Gomez",
          idEmpleado: 2,
          empleado: "Cajero Prueba",
          canalPedido: "Presencial",
          estado: "Despachada",
          fechaOrden: ISODate("2026-07-04T11:20:00Z"),
          total: NumberDecimal("23.80"),
          observacion: "Venta entregada en tienda",
          detalles: [
            {
              idDetalleVenta: 1,
              idProducto: 10,
              producto: "Ambroxol jarabe",
              cantidad: 2,
              precioUnitario: NumberDecimal("11.90"),
              subtotal: NumberDecimal("23.80")
            }
          ]
        }
      },
      upsert: true
    }
  },
  {
    updateOne: {
      filter: { idOrdenVenta: 5 },
      update: {
        $set: {
          idOrdenVenta: 5,
          idCliente: 5,
          cliente: "Carlos Ramirez",
          idEmpleado: 1,
          empleado: "Empleado Prueba",
          canalPedido: "WhatsApp",
          estado: "Anulada",
          fechaOrden: ISODate("2026-07-05T18:10:00Z"),
          total: NumberDecimal("8.90"),
          observacion: "Venta anulada para prueba de estados",
          detalles: [
            {
              idDetalleVenta: 1,
              idProducto: 24,
              producto: "Alcohol gel 70%",
              cantidad: 1,
              precioUnitario: NumberDecimal("8.90"),
              subtotal: NumberDecimal("8.90")
            }
          ]
        }
      },
      upsert: true
    }
  },
  {
    updateOne: {
      filter: { idOrdenVenta: 6 },
      update: {
        $set: {
          idOrdenVenta: 6,
          idCliente: 6,
          cliente: "Rosa Salazar",
          idEmpleado: 1,
          empleado: "Empleado Prueba",
          canalPedido: "Telefono",
          estado: "Rechazada",
          fechaOrden: ISODate("2026-07-06T10:30:00Z"),
          total: NumberDecimal("13.90"),
          observacion: "Venta rechazada para prueba de estados",
          detalles: [
            {
              idDetalleVenta: 1,
              idProducto: 4,
              producto: "Diclofenaco gel 1%",
              cantidad: 1,
              precioUnitario: NumberDecimal("13.90"),
              subtotal: NumberDecimal("13.90")
            }
          ]
        }
      },
      upsert: true
    }
  }
]);
