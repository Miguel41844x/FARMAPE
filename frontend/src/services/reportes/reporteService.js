import apiClient from "../api/apiClient";

export const obtenerResumenReportes = () =>
    apiClient("/reportes/resumen", {
        mensajeError: "No se pudo obtener el resumen gerencial",
    });

export const listarInformesGerenciales = () =>
    apiClient("/reportes/informes", {
        mensajeError: "No se pudieron obtener los informes",
    });

export const registrarInformeGerencial = (datosInforme) =>
    apiClient("/reportes/informes", {
        method: "POST",
        body: JSON.stringify(datosInforme),
        mensajeError: "No se pudo registrar el informe",
    });

export const listarAccionesGerenciales = () =>
    apiClient("/reportes/acciones", {
        mensajeError: "No se pudieron obtener las acciones gerenciales",
    });

export const registrarAccionGerencial = (datosAccion) =>
    apiClient("/reportes/acciones", {
        method: "POST",
        body: JSON.stringify(datosAccion),
        mensajeError: "No se pudo registrar la acción gerencial",
    });

export const actualizarEstadoAccionGerencial = (idAccion, estado) =>
    apiClient(`/reportes/acciones/${idAccion}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estado }),
        mensajeError: "No se pudo actualizar la acción gerencial",
    });
