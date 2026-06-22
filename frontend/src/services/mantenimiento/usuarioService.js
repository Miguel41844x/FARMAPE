import { API_URL } from "../../config/api";

export const obtenerRoles = async (incluirInactivos = false) => {
    const token = localStorage.getItem("token");
    const response = await fetch(`${API_URL}/roles?incluirInactivos=${incluirInactivos}`, {
        headers: { Authorization: `Bearer ${token}` },
    });

    if (!response.ok) {
        throw new Error("No se pudieron obtener los roles");
    }

    return response.json();
};

const roleRequest = async (path, options = {}) => {
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
        throw new Error(body?.message || "No se pudo completar la operación");
    }
    return response.status === 204 ? null : response.json();
};

export const obtenerPermisos = () => roleRequest("/permisos");
export const crearRol = (rol) => roleRequest("/roles", {
    method: "POST",
    body: JSON.stringify(rol),
});
export const actualizarRol = (idRol, rol) => roleRequest(`/roles/${idRol}`, {
    method: "PUT",
    body: JSON.stringify(rol),
});
export const asignarPermisosRol = (idRol, idPermisos) => roleRequest(`/roles/${idRol}/permisos`, {
    method: "PUT",
    body: JSON.stringify({ idPermisos }),
});
export const cambiarEstadoRol = (idRol, activo) => roleRequest(`/roles/${idRol}/estado`, {
    method: "PATCH",
    body: JSON.stringify({ activo }),
});
export const eliminarRol = (idRol) => roleRequest(`/roles/${idRol}`, { method: "DELETE" });

export const obtenerUsuarios = async () => {
    const token = localStorage.getItem("token");
    const response = await fetch(`${API_URL}/usuarios`, {
        headers: { Authorization: `Bearer ${token}` },
    });

    if (!response.ok) {
        throw new Error("Error al obtener usuarios");
    }

    return response.json();
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

export const actualizarEstadoCuenta = async (idCuenta, estado) => {
    const token = localStorage.getItem("token");
    const response = await fetch(`${API_URL}/usuarios/${idCuenta}/estado`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({ estado }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo actualizar el estado de la cuenta");
    }

    return response.json();
};
