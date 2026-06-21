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

export const listarIngresosAlmacen = async () => {
    const response = await fetch(`${API_URL}/almacen/ingresos`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "Error al listar ingresos de almacén"
    );
};

export const registrarIngresoAlmacen = async (datosIngreso) => {
    const response = await fetch(`${API_URL}/almacen/ingresos`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(datosIngreso),
    });

    return await validarRespuesta(
        response,
        "Error al registrar ingreso de almacén"
    );
};

export const listarVerificacionesProductos = async () => {
    const response = await fetch(`${API_URL}/almacen/verificaciones`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "Error al listar verificaciones de productos"
    );
};

export const confirmarVerificacionProducto = async (idVerificacion) => {
    const response = await fetch(
        `${API_URL}/almacen/verificaciones/${idVerificacion}/confirmar`,
        {
            method: "PATCH",
            headers: getAuthHeaders(),
        }
    );

    return await validarRespuesta(
        response,
        "Error al confirmar verificación"
    );
};

export const observarVerificacionProducto = async (idVerificacion) => {
    const response = await fetch(
        `${API_URL}/almacen/verificaciones/${idVerificacion}/observar`,
        {
            method: "PATCH",
            headers: getAuthHeaders(),
        }
    );

    return await validarRespuesta(
        response,
        "Error al observar verificación"
    );
};

export const obtenerInformeAlmacen = async (periodo = "HOY") => {
    const response = await fetch(`${API_URL}/almacen/informe?periodo=${periodo}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "Error al obtener informe de almacén"
    );
};