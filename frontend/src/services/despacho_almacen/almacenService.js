import apiClient from "../api/apiClient";

export const listarIngresosAlmacen = () =>
    apiClient("/almacen/ingresos", {
        mensajeError: "Error al listar ingresos de almacén",
    });

export const registrarIngresoAlmacen = (datosIngreso) =>
    apiClient("/almacen/ingresos", {
        method: "POST",
        body: JSON.stringify(datosIngreso),
        mensajeError: "Error al registrar ingreso de almacén",
    });

export const listarVerificacionesProductos = () =>
    apiClient("/almacen/verificaciones", {
        mensajeError: "Error al listar verificaciones de productos",
    });

export const confirmarVerificacionProducto = (idVerificacion) =>
    apiClient(`/almacen/verificaciones/${idVerificacion}/confirmar`, {
        method: "PATCH",
        mensajeError: "Error al confirmar verificación",
    });

export const observarVerificacionProducto = (idVerificacion) =>
    apiClient(`/almacen/verificaciones/${idVerificacion}/observar`, {
        method: "PATCH",
        mensajeError: "Error al observar verificación",
    });

export const obtenerInformeAlmacen = (periodo = "HOY") =>
    apiClient(`/almacen/informe?periodo=${periodo}`, {
        mensajeError: "Error al obtener informe de almacén",
    });
