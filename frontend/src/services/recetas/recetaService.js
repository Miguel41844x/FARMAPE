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

const request = async (path, options = {}, mensajeDefault) => {
    const response = await fetch(`${API_URL}${path}`, {
        ...options,
        headers: {
            ...getAuthHeaders(),
            ...options.headers,
        },
    });
    if (!response.ok) throw new Error(await parseError(response, mensajeDefault || "No se pudo completar la operación"));
    return response.status === 204 ? null : response.json();
};

export const obtenerRecetas = async () => request("/formulas/recetas-listas", { method: "GET" }, "No se pudieron obtener las recetas magistrales");

export const obtenerInsumosDisponibles = async () => request("/formulas/insumos", { method: "GET" }, "No se pudieron obtener los insumos disponibles");

export const registrarFormulaPresupuestada = async (datosPresupuesto) => request("/formulas/presupuestar", {
    method: "POST",
    body: JSON.stringify(datosPresupuesto),
}, "No se pudo registrar el presupuesto de la fórmula");

export const registrarRecetaMedica = async (datosReceta) => request("/formulas/recetas", {
    method: "POST",
    body: JSON.stringify(datosReceta),
}, "No se pudo registrar la receta médica");

export const cambiarEstadoReceta = async (idReceta, estado, observacion = "") => request(`/formulas/recetas/${idReceta}/estado`, {
    method: "PATCH",
    body: JSON.stringify({ estado, observacion }),
}, "No se pudo actualizar el estado de la receta");

export const buscarClientePorDocumento = async (documento) => request(`/clientes/documento/${documento}`, { method: "GET" }, "Cliente no encontrado en la base de datos");

export const registrarCliente = async (datosCliente) => {
    const payload = {
        dniRuc: datosCliente.dniRuc || datosCliente.documento,
        tipoCliente: datosCliente.tipoCliente || "Natural",
        nombres: datosCliente.nombres,
        apellidos: datosCliente.apellidos,
        telefono: datosCliente.telefono,
        whatsapp: datosCliente.whatsapp,
        email: datosCliente.email,
        direccion: datosCliente.direccion,
    };
    return request("/clientes", {
        method: "POST",
        body: JSON.stringify(payload),
    }, "No se pudo registrar al nuevo cliente");
};
