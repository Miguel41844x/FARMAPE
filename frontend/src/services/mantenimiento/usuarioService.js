import apiClient from "../api/apiClient";

export const obtenerRoles = (incluirInactivos = false) =>
    apiClient(`/roles?incluirInactivos=${incluirInactivos}`, {
        mensajeError: "No se pudieron obtener los roles",
    });

export const obtenerPermisos = () =>
    apiClient("/permisos", { mensajeError: "No se pudieron obtener los permisos" });

export const crearRol = (rol) =>
    apiClient("/roles", { method: "POST", body: JSON.stringify(rol) });

export const actualizarRol = (idRol, rol) =>
    apiClient(`/roles/${idRol}`, { method: "PUT", body: JSON.stringify(rol) });

export const asignarPermisosRol = (idRol, idPermisos) =>
    apiClient(`/roles/${idRol}/permisos`, {
        method: "PUT",
        body: JSON.stringify({ idPermisos }),
    });

export const cambiarEstadoRol = (idRol, activo) =>
    apiClient(`/roles/${idRol}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ activo }),
    });

export const eliminarRol = (idRol) =>
    apiClient(`/roles/${idRol}`, { method: "DELETE" });

export const obtenerUsuarios = () =>
    apiClient("/usuarios", { mensajeError: "Error al obtener usuarios" });

export const crearUsuario = (usuarioRequest) =>
    apiClient("/usuarios", {
        method: "POST",
        body: JSON.stringify(usuarioRequest),
        mensajeError: "No se pudo registrar el usuario",
    });

export const actualizarUsuarioCompleto = (idCuenta, usuarioRequest) =>
    apiClient(`/usuarios/${idCuenta}`, {
        method: "PUT",
        body: JSON.stringify(usuarioRequest),
        mensajeError: "No se pudo actualizar el usuario",
    });

export const cambiarClaveUsuario = (idCuenta, nuevaClave) =>
    apiClient(`/usuarios/${idCuenta}/clave`, {
        method: "PATCH",
        body: JSON.stringify({ nuevaClave }),
        mensajeError: "No se pudo cambiar la contraseña",
    });

export const actualizarTrabajador = (idTrabajador, trabajadorRequest) =>
    apiClient(`/trabajadores/${idTrabajador}`, {
        method: "PUT",
        body: JSON.stringify(trabajadorRequest),
        mensajeError: "No se pudo actualizar el trabajador",
    });

export const actualizarEstadoTrabajador = (idTrabajador, estado) =>
    apiClient(`/trabajadores/${idTrabajador}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estado }),
        mensajeError: "No se pudo actualizar el estado del trabajador",
    });

export const actualizarEstadoCuenta = (idCuenta, estado) =>
    apiClient(`/usuarios/${idCuenta}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estado }),
        mensajeError: "No se pudo actualizar el estado de la cuenta",
    });
