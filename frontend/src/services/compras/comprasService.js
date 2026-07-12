import apiClient from "../api/apiClient";

export const obtenerOrdenesCompra = () =>
    apiClient("/ordenes-compra", {
        mensajeError: "No se pudieron obtener las órdenes de compra",
    });

export const crearOrdenCompra = (orden) =>
    apiClient("/ordenes-compra", {
        method: "POST",
        body: JSON.stringify(orden),
        mensajeError: "No se pudo crear la orden de compra",
    });

export const obtenerFacturasProveedor = () =>
    apiClient("/facturas-proveedor", {
        mensajeError: "No se pudieron obtener las facturas",
    });

export const registrarFacturaProveedor = (factura) =>
    apiClient("/facturas-proveedor", {
        method: "POST",
        body: JSON.stringify(factura),
        mensajeError: "No se pudo registrar la factura",
    });

export const obtenerNotasCreditoProveedor = () =>
    apiClient("/notas-credito-proveedor", {
        mensajeError: "No se pudieron obtener las notas de crédito",
    });

export const registrarNotaCreditoProveedor = (nota) =>
    apiClient("/notas-credito-proveedor", {
        method: "POST",
        body: JSON.stringify(nota),
        mensajeError: "No se pudo registrar la nota de crédito",
    });

export const obtenerPagosProveedor = () =>
    apiClient("/pagos-proveedor", {
        mensajeError: "No se pudieron obtener los pagos",
    });

export const registrarPagoProveedor = (pago) =>
    apiClient("/pagos-proveedor", {
        method: "POST",
        body: JSON.stringify(pago),
        mensajeError: "No se pudo registrar el pago al proveedor",
    });
