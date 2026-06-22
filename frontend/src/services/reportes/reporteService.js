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

export const obtenerResumenReportes = async () => {
    const response = await fetch(`${API_URL}/reportes/resumen`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(response, "No se pudo obtener el resumen gerencial");
};

export const listarInformesGerenciales = async () => {
    const response = await fetch(`${API_URL}/reportes/informes`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(response, "No se pudieron obtener los informes");
};

export const registrarInformeGerencial = async (datosInforme) => {
    const response = await fetch(`${API_URL}/reportes/informes`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(datosInforme),
    });

    return await validarRespuesta(response, "No se pudo registrar el informe");
};

export const listarAccionesGerenciales = async () => {
    const response = await fetch(`${API_URL}/reportes/acciones`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(response, "No se pudieron obtener las acciones gerenciales");
};

export const registrarAccionGerencial = async (datosAccion) => {
    const response = await fetch(`${API_URL}/reportes/acciones`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(datosAccion),
    });

    return await validarRespuesta(response, "No se pudo registrar la acción gerencial");
};

export const actualizarEstadoAccionGerencial = async (idAccion, estado) => {
    const response = await fetch(`${API_URL}/reportes/acciones/${idAccion}/estado`, {
        method: "PATCH",
        headers: getAuthHeaders(),
        body: JSON.stringify({ estado }),
    });

    return await validarRespuesta(response, "No se pudo actualizar la acción gerencial");
};
