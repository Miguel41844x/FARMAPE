import { API_URL } from "../../config/api";

export const registrarVenta = async (ventaRequest) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/ventas`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(ventaRequest),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo generar el ticket");
    }

    return await response.json();
};

export const obtenerUltimasOrdenes = async () => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/ventas/ultimas`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error("No se pudieron cargar las órdenes");
    }

    return await response.json();
};