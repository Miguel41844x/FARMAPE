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

export const obtenerProductos = async () => {
    const response = await fetch(`${API_URL}/productos`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "No se pudieron obtener los productos"
    );
};

export const crearProducto = async (producto) => {
    const response = await fetch(`${API_URL}/productos`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(producto),
    });

    return await validarRespuesta(
        response,
        "No se pudo crear el producto"
    );
};

export const actualizarProducto = async (idProducto, producto) => {
    const response = await fetch(`${API_URL}/productos/${idProducto}`, {
        method: "PUT",
        headers: getAuthHeaders(),
        body: JSON.stringify(producto),
    });

    return await validarRespuesta(
        response,
        "No se pudo actualizar el producto"
    );
};

export const actualizarEstadoProducto = async (idProducto, estado) => {
    const response = await fetch(`${API_URL}/productos/${idProducto}/estado`, {
        method: "PATCH",
        headers: getAuthHeaders(),
        body: JSON.stringify({ estado }),
    });

    return await validarRespuesta(
        response,
        "No se pudo actualizar el estado del producto"
    );
};

export const obtenerCategoriasProducto = async () => {
    const response = await fetch(`${API_URL}/categorias`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    return await validarRespuesta(
        response,
        "No se pudieron obtener las categorías"
    );
};