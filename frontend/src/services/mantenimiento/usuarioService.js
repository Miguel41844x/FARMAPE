import { API_URL } from "../../config/api";

export const obtenerUsuarios = async () => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/trabajadores`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error("Error al obtener usuarios");
    }

    return await response.json();
};

export const crearUsuario = async (usuarioRequest) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/usuarios`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(usuarioRequest),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo registrar el usuario");
    }

    return await response.json();
};

export const actualizarTrabajador = async (idTrabajador, trabajadorRequest) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/trabajadores/${idTrabajador}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(trabajadorRequest),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo actualizar el trabajador");
    }

    return await response.json();
};

export const actualizarEstadoTrabajador = async (idTrabajador, estado) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/trabajadores/${idTrabajador}/estado`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
            estado,
        }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo actualizar el estado del trabajador");
    }

    return await response.json();
};