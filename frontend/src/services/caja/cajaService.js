import { API_URL } from "../../config/api";

const getAuthHeaders = () => {
    const token = localStorage.getItem("token");

    return {
        Authorization: `Bearer ${token}`,
    };
};

export const listarOrdenesPendientesCaja = async () => {
    const response = await fetch(`${API_URL}/caja/ordenes-pendientes`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error("No se pudieron obtener las órdenes pendientes");
    }

    return await response.json();
};

export const obtenerOrdenCaja = async (idOrdenVenta) => {
    const response = await fetch(`${API_URL}/caja/ordenes/${idOrdenVenta}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error("No se pudo obtener el detalle de la orden");
    }

    return await response.json();
};

export const registrarPagoCaja = async (idOrdenVenta, datosPago) => {
    const response = await fetch(`${API_URL}/caja/ordenes/${idOrdenVenta}/pagar`, {
        method: "POST",
        headers: {
            ...getAuthHeaders(),
            "Content-Type": "application/json",
        },
        body: JSON.stringify(datosPago),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo registrar el pago");
    }

    return await response.json();
};