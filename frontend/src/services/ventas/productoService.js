import { API_URL } from "../../config/api"

export const obtenerProductos = async () => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/productos`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error("Error al obtener productos");
    }

    return await response.json();
};