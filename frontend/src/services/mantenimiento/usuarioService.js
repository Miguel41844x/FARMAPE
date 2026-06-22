import { API_URL } from "../../config/api";

const getAuthHeaders = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
});

const parseError = async (response, mensajeDefault) => {
    const text = await response.text();
    if (!text) return mensajeDefault;
    try {
        const json = JSON.parse(text);
        return json.message || json.error || text;
    } catch {
        return text;
    }
};

const request = async (path, options = {}, mensajeDefault = "No se pudo completar la operación") => {
    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: {
            ...getAuthHeaders(),
            ...options.headers,
        },
    });

    if (!response.ok) {
        throw new Error(await parseError(response, mensajeDefault));
    }

    return response.status === 204 ? null : response.json();
};

export const obtenerRoles = async (incluirInactivos = false) =>
    request(`/roles?incluirInactivos=${incluirInactivos}`, { method: "GET" }, "No se pudieron obtener los roles");

export const obtenerPermisos = () => request("/permisos", { method: "GET" }, "No se pudieron obtener los permisos");

export const crearRol = (rol) => request("/roles", {
    method: "POST",
    body: JSON.stringify(rol),
});

export const actualizarRol = (idRol, rol) => request(`/roles/${idRol}`, {
    method: "PUT",
    body: JSON.stringify(rol),
});

export const asignarPermisosRol = (idRol, idPermisos) => request(`/roles/${idRol}/permisos`, {
    method: "PUT",
    body: JSON.stringify({ idPermisos }),
});

export const cambiarEstadoRol = (idRol, activo) => request(`/roles/${idRol}/estado`, {
    method: "PATCH",
    body: JSON.stringify({ activo }),
});

export const eliminarRol = (idRol) => request(`/roles/${idRol}`, { method: "DELETE" });

export const obtenerUsuarios = async () => request("/usuarios", { method: "GET" }, "Error al obtener usuarios");

export const crearUsuario = async (usuarioRequest) => request("/usuarios", {
    method: "POST",
    body: JSON.stringify(usuarioRequest),
}, "No se pudo registrar el usuario");

export const actualizarUsuarioCompleto = async (idCuenta, usuarioRequest) => request(`/usuarios/${idCuenta}`, {
    method: "PUT",
    body: JSON.stringify(usuarioRequest),
}, "No se pudo actualizar el usuario");

export const cambiarClaveUsuario = async (idCuenta, nuevaClave) => request(`/usuarios/${idCuenta}/clave`, {
    method: "PATCH",
    body: JSON.stringify({ nuevaClave }),
}, "No se pudo cambiar la contraseña");

export const actualizarTrabajador = async (idTrabajador, trabajadorRequest) => request(`/trabajadores/${idTrabajador}`, {
    method: "PUT",
    body: JSON.stringify(trabajadorRequest),
}, "No se pudo actualizar el trabajador");

export const actualizarEstadoTrabajador = async (idTrabajador, estado) => request(`/trabajadores/${idTrabajador}/estado`, {
    method: "PATCH",
    body: JSON.stringify({ estado }),
}, "No se pudo actualizar el estado del trabajador");

export const actualizarEstadoCuenta = async (idCuenta, estado) => request(`/usuarios/${idCuenta}/estado`, {
    method: "PATCH",
    body: JSON.stringify({ estado }),
}, "No se pudo actualizar el estado de la cuenta");
