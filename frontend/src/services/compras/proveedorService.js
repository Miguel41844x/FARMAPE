import apiClient from "../api/apiClient";

export const obtenerProveedores = () =>
    apiClient("/proveedores", {
        mensajeError: "No se pudo completar la operación con el proveedor",
    });

export const crearProveedor = (proveedor) =>
    apiClient("/proveedores", {
        method: "POST",
        body: JSON.stringify(proveedor),
        mensajeError: "No se pudo completar la operación con el proveedor",
    });

export const actualizarProveedor = (idProveedor, proveedor) =>
    apiClient(`/proveedores/${idProveedor}`, {
        method: "PUT",
        body: JSON.stringify(proveedor),
        mensajeError: "No se pudo completar la operación con el proveedor",
    });

export const eliminarProveedor = (idProveedor) =>
    apiClient(`/proveedores/${idProveedor}`, {
        method: "DELETE",
        mensajeError: "No se pudo completar la operación con el proveedor",
    });
