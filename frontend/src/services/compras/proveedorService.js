import { API_URL } from "../../config/api";

const getHeaders = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
});

const request = async (path = "", options = {}) => {
    const response = await fetch(`${API_URL}/proveedores${path}`, {
        ...options,
        headers: getHeaders(),
    });

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "No se pudo completar la operación con el proveedor");
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
