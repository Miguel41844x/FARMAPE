import { API_URL } from "../../config/api";

export const obtenerClientes = async () => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/clientes`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error("Error al obtener clientes");
    }

    return await response.json();
};

export const buscarClientePorDocumento = async (documento) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/clientes/documento/${documento}`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error("Cliente no encontrado");
    }

    return await response.json();
};

export const registrarCliente = async (cliente) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/clientes`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(cliente),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo registrar el cliente");
    }

    return await response.json();
};

