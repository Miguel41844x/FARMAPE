import { API_URL } from "../../config/api";

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

export const listarSolicitudesRestablecimiento = async () => {
    const response = await fetch(`${API_URL}/auth/solicitudes-restablecimiento`, {
        headers: { Authorization: `Bearer ${localStorage.getItem("token")}` },
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudieron obtener las solicitudes"));
    }

    return response.json();
};
