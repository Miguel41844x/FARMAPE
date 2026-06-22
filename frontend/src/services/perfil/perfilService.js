import { API_URL } from "../../config/api";

const perfilRequest = async (path, options = {}) => {
    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
            "Content-Type": "application/json",
            ...options.headers,
        },
    });

    if (!response.ok) {
        const body = await response.json().catch(() => null);
        const text = body ? null : await response.text().catch(() => null);
        throw new Error(body?.message || text || "No se pudo completar la operación");
    }

    return response.json();
};

export const obtenerMiPerfil = () => perfilRequest("/perfil");

export const actualizarMiPerfil = (perfil) => perfilRequest("/perfil", {
    method: "PUT",
    body: JSON.stringify(perfil),
});
