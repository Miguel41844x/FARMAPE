import { API_URL } from "../../config/api";

const getAuthHeaders = () => {
    const token = localStorage.getItem("token");

    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
};

const validarRespuesta = async (response, mensajeError) => {
    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || mensajeError);
    }

    return await response.json();
};

export const listarOrdenesTienda = async () => {
    const response = await fetch(`${API_URL}/despacho/ordenes-tienda`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "Error al listar órdenes para entrega en tienda"
    );
};

export const entregarOrdenTienda = async (idOrdenVenta) => {
    const response = await fetch(
        `${API_URL}/despacho/ordenes-tienda/${idOrdenVenta}/entregar`,
        {
            method: "PATCH",
            headers: getAuthHeaders(),
        }
    );

    return await validarRespuesta(
        response,
        "Error al marcar la orden como entregada"
    );
};

export const listarRepartosDomicilio = async () => {
    const response = await fetch(`${API_URL}/despacho/repartos`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "Error al listar repartos a domicilio"
    );
};

export const marcarRepartoEntregado = async (idReparto) => {
    const response = await fetch(
        `${API_URL}/despacho/repartos/${idReparto}/entregar`,
        {
            method: "PATCH",
            headers: getAuthHeaders(),
        }
    );

    return await validarRespuesta(
        response,
        "Error al marcar reparto como entregado"
    );
};