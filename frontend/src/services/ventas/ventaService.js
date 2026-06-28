import apiClient from "../api/apiClient";

export const registrarVenta = (ventaRequest) =>
    apiClient("/ventas", {
        method: "POST",
        body: JSON.stringify(ventaRequest),
        mensajeError: "No se pudo generar el ticket",
    });

export const confirmarVenta = (idOrdenVenta) =>
    apiClient(`/ventas/${idOrdenVenta}/confirmar`, {
        method: "PATCH",
        mensajeError: "No se pudo confirmar la orden",
    });

export const rechazarVenta = (idOrdenVenta) =>
    apiClient(`/ventas/${idOrdenVenta}/rechazar`, {
        method: "PATCH",
        mensajeError: "No se pudo rechazar la orden",
    });

export const obtenerUltimasOrdenes = () =>
    apiClient("/ventas/ultimas", {
        mensajeError: "No se pudieron cargar las órdenes",
    });
