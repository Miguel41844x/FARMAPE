import { API_URL } from "../../config/api";

const getHeaders = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
});

const request = async (path, options = {}) => {
    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: getHeaders(),
    });

    if (!response.ok) {
        const message = await response.text();
        throw new Error(message || "No se pudo completar la operación");
    }

    if (response.status === 204) return null;
    return response.json();
};

export const obtenerOrdenesCompra = () => request("/ordenes-compra");
export const crearOrdenCompra = (orden) => request("/ordenes-compra", {
    method: "POST",
    body: JSON.stringify(orden),
});

export const obtenerFacturasProveedor = () => request("/facturas-proveedor");
export const registrarFacturaProveedor = (factura) => request("/facturas-proveedor", {
    method: "POST",
    body: JSON.stringify(factura),
});

export const obtenerNotasCreditoProveedor = () => request("/notas-credito-proveedor");
export const registrarNotaCreditoProveedor = (nota) => request("/notas-credito-proveedor", {
    method: "POST",
    body: JSON.stringify(nota),
});

export const obtenerPagosProveedor = () => request("/pagos-proveedor");
export const registrarPagoProveedor = (pago) => request("/pagos-proveedor", {
    method: "POST",
    body: JSON.stringify(pago),
});
