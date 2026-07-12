import apiClient from "../api/apiClient";

export const obtenerClientes = () =>
    apiClient("/clientes", {
        mensajeError: "Error al obtener clientes",
    });

export const buscarClientePorDocumento = (documento) =>
    apiClient(`/clientes/documento/${documento}`, {
        mensajeError: "Cliente no encontrado",
    });

export const registrarCliente = (cliente) =>
    apiClient("/clientes", {
        method: "POST",
        body: JSON.stringify(cliente),
        mensajeError: "No se pudo registrar el cliente",
    });
