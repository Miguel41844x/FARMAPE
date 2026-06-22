import { API_URL } from "../../config/api";

const getHeaders = () => ({
    Authorization: `Bearer ${localStorage.getItem("token")}`,
    "Content-Type": "application/json",
});

const parseError = async (response) => {
    const text = await response.text().catch(() => "");
    if (!text) return "No se pudo completar la operación";

    try {
        const json = JSON.parse(text);
        return json.message || json.error || text;
    } catch {
        return text;
    }
};

const request = async (path, options = {}) => {
    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: getHeaders(),
    });

    if (response.status === 401) {
        localStorage.removeItem("token");
        localStorage.removeItem("user");
        window.location.href = "/login";
        throw new Error("Sesión expirada. Inicia sesión nuevamente.");
    }

    if (!response.ok) {
        throw new Error(await parseError(response));
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
