import apiClient from "../api/apiClient";

const buildQuery = (filtros = {}) => {
    const params = new URLSearchParams();
    Object.entries(filtros).forEach(([key, value]) => {
        if (value !== null && value !== undefined && String(value).trim() !== "")
            params.append(key, value);
    });
    const query = params.toString();
    return query ? `?${query}` : "";
};

export const obtenerResumenAuditoria = () =>
    apiClient("/auditoria/resumen", {
        mensajeError: "No se pudo obtener el resumen de auditoría",
    });

export const listarEventosAuditoria = (filtros = {}) =>
    apiClient(`/auditoria/eventos${buildQuery(filtros)}`, {
        mensajeError: "No se pudieron obtener los eventos de auditoría",
    });

export const registrarEventoAuditoria = (evento) =>
    apiClient("/auditoria/eventos", {
        method: "POST",
        body: JSON.stringify({ ...evento, tipoEvento: "OBSERVACION" }),
        mensajeError: "No se pudo registrar la observación de auditoría",
    });

export const exportarEventosAuditoria = (filtros = {}) =>
    apiClient(`/auditoria/eventos/exportar${buildQuery(filtros)}`, {
        responseType: "blob",
        mensajeError: "No se pudo exportar auditoría",
    });
