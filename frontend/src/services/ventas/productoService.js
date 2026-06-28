import apiClient from "../api/apiClient";

export const obtenerProductos = () =>
    apiClient("/productos/activos", {
        mensajeError: "Error al obtener productos",
    });
