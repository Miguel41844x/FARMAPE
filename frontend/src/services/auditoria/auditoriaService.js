import { API_URL } from "../../config/api";

const getAuthHeaders = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
});

const parseError = async (response, mensajeDefault) => {
    const text = await response.text();
    if (!text) return mensajeDefault;
    try {
        const json = JSON.parse(text);
        return json.message || json.error || text;
    } catch {
        return text;
    }
};

const buildQuery = (filtros = {}) => {
    const params = new URLSearchParams();
    Object.entries(filtros).forEach(([key, value]) => {
        if (value !== null && value !== undefined && String(value).trim() !== "") params.append(key, value);
    });
    const query = params.toString();
    return query ? `?${query}` : "";
};

export const obtenerResumenAuditoria = async () => {
    const response = await fetch(`${API_URL}/auditoria/resumen`, { method: "GET", headers: getAuthHeaders() });
    if (!response.ok) throw new Error(await parseError(response, "No se pudo obtener el resumen de auditoría"));
    return response.json();
};

export const listarEventosAuditoria = async (filtros = {}) => {
    const response = await fetch(`${API_URL}/auditoria/eventos${buildQuery(filtros)}`, { method: "GET", headers: getAuthHeaders() });
    if (!response.ok) throw new Error(await parseError(response, "No se pudieron obtener los eventos de auditoría"));
    return response.json();
};

export const registrarEventoAuditoria = async (evento) => {
    const response = await fetch(`${API_URL}/auditoria/eventos`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ ...evento, tipoEvento: "OBSERVACION" }),
    });
    if (!response.ok) throw new Error(await parseError(response, "No se pudo registrar la observación de auditoría"));
    return response.json();
};

export const exportarEventosAuditoria = async (filtros = {}) => {
    const response = await fetch(`${API_URL}/auditoria/eventos/exportar${buildQuery(filtros)}`, {
        method: "GET",
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });
    if (!response.ok) throw new Error(await parseError(response, "No se pudo exportar auditoría"));
    return response.blob();
};
