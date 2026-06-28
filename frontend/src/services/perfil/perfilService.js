import apiClient from "../api/apiClient";

export const obtenerMiPerfil = () =>
    apiClient("/perfil", {
        mensajeError: "No se pudo completar la operación",
    });

export const actualizarMiPerfil = (perfil) =>
    apiClient("/perfil", {
        method: "PUT",
        body: JSON.stringify(perfil),
        mensajeError: "No se pudo completar la operación",
    });
