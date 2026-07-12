import apiClient from "../api/apiClient";

export const listarOrdenesTienda = () =>
    apiClient("/despacho/ordenes-tienda", {
        mensajeError: "Error al listar órdenes para entrega en tienda",
    });

export const entregarOrdenTienda = (idOrdenVenta) =>
    apiClient(`/despacho/ordenes-tienda/${idOrdenVenta}/entregar`, {
        method: "PATCH",
        mensajeError: "Error al marcar la orden como entregada",
    });

export const listarRepartosDomicilio = () =>
    apiClient("/despacho/repartos", {
        mensajeError: "Error al listar repartos a domicilio",
    });

export const marcarRepartoEntregado = (idReparto) =>
    apiClient(`/despacho/repartos/${idReparto}/entregar`, {
        method: "PATCH",
        mensajeError: "Error al marcar reparto como entregado",
    });
