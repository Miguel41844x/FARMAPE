import apiClient from "../api/apiClient";

export const obtenerProductos = () =>
    apiClient("/productos", {
        mensajeError: "No se pudieron obtener los productos",
    });

export const crearProducto = (producto) =>
    apiClient("/productos", {
        method: "POST",
        body: JSON.stringify(producto),
        mensajeError: "No se pudo crear el producto",
    });

export const actualizarProducto = (idProducto, producto) =>
    apiClient(`/productos/${idProducto}`, {
        method: "PUT",
        body: JSON.stringify(producto),
        mensajeError: "No se pudo actualizar el producto",
    });

export const actualizarEstadoProducto = (idProducto, estado) =>
    apiClient(`/productos/${idProducto}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estado }),
        mensajeError: "No se pudo actualizar el estado del producto",
    });

export const obtenerCategoriasProducto = () =>
    apiClient("/categorias", {
        mensajeError: "No se pudieron obtener las categorías",
    });
