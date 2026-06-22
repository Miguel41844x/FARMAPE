import { API_URL } from "../../config/api";

const getHeaders = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
});

const parseError = async (response) => {
    const text = await response.text().catch(() => "");
    if (!text) return "No se pudo completar la operación con el proveedor";

    try {
        const json = JSON.parse(text);
        return json.message || json.error || text;
    } catch {
        return text;
    }
};

const request = async (path = "", options = {}) => {
    const response = await fetch(`${API_URL}/proveedores${path}`, {
        ...options,
        headers: getHeaders(),
    });

    if (response.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
        throw new Error("Sesión expirada. Inicia sesión nuevamente.");
    }

    if (!response.ok) {
        throw new Error(await parseError(response));
    }

    if (response.status === 204 || options.method === "DELETE") return null;
    return response.json();
};

export const obtenerProveedores = () => request();
export const crearProveedor = (proveedor) => request("", {
    method: "POST",
    body: JSON.stringify(proveedor),
});
export const actualizarProveedor = (idProveedor, proveedor) => request(`/${idProveedor}`, {
    method: "PUT",
    body: JSON.stringify(proveedor),
});
export const eliminarProveedor = (idProveedor) => request(`/${idProveedor}`, {
    method: "DELETE",
});
