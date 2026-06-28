import { API_URL } from "../../config/api";
import apiClient from "../api/apiClient";

// parseError solo para la ruta pública (no pasa por apiClient)
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

// Ruta pública — sin token, fetch directo
export const solicitarRestablecimiento = async (payload) => {
    const response = await fetch(`${API_URL}/auth/solicitar-restablecimiento`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
    });
    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudo registrar la solicitud"));
    }
    return response.json();
};

// Ruta protegida — usa apiClient
export const listarSolicitudesRestablecimiento = () =>
    apiClient("/auth/solicitudes-restablecimiento", {
        mensajeError: "No se pudieron obtener las solicitudes",
    });
