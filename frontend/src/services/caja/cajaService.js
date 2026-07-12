import apiClient from "../api/apiClient";

export const listarOrdenesPendientesCaja = () =>
    apiClient("/caja/ordenes-pendientes", {
        mensajeError: "No se pudieron obtener las órdenes pendientes",
    });

export const obtenerOrdenCaja = (idOrdenVenta) =>
    apiClient(`/caja/ordenes/${idOrdenVenta}`, {
        mensajeError: "No se pudo obtener el detalle de la orden",
    });

export const registrarPagoCaja = (idOrdenVenta, datosPago) =>
    apiClient(`/caja/ordenes/${idOrdenVenta}/pagar`, {
        method: "POST",
        body: JSON.stringify(datosPago),
        mensajeError: "No se pudo registrar el pago",
    });
