import { API_URL } from "../../config/api";

const getAuthHeaders = () => ({
    "Content-Type": "application/json",
    Authorization: `Bearer ${localStorage.getItem("token")}`,
});

const validar = async (response, mensaje) => {
    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || mensaje);
    }
    return response.json();
};

export const registrarVenta = async (ventaRequest) => {
    const response = await fetch(`${API_URL}/ventas`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(ventaRequest),
    });

    return validar(response, "No se pudo generar el ticket");
};

export const confirmarVenta = async (idOrdenVenta) => {
    const response = await fetch(`${API_URL}/ventas/${idOrdenVenta}/confirmar`, {
        method: "PATCH",
        headers: getAuthHeaders(),
    });

    return validar(response, "No se pudo confirmar la orden");
};

export const rechazarVenta = async (idOrdenVenta) => {
    const response = await fetch(`${API_URL}/ventas/${idOrdenVenta}/rechazar`, {
        method: "PATCH",
        headers: getAuthHeaders(),
    });

    return validar(response, "No se pudo rechazar la orden");
};

export const obtenerUltimasOrdenes = async () => {
    const response = await fetch(`${API_URL}/ventas/ultimas`, {
        headers: getAuthHeaders(),
    });

    return validar(response, "No se pudieron cargar las órdenes");
};
