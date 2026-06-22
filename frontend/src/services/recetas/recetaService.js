import { API_URL } from "../../config/api";

const getAuthHeaders = () => {
    const token = localStorage.getItem("token");
    return {
        Authorization: `Bearer ${token}`,
        "Content-Type": "application/json",
    };
};

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

export const obtenerRecetas = async () => {
    const response = await fetch(`${API_URL}/formulas/recetas-listas`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudieron obtener las recetas magistrales"));
    }

    return await response.json();
};

export const obtenerInsumosDisponibles = async () => {
    const response = await fetch(`${API_URL}/formulas/insumos`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudieron obtener los insumos disponibles"));
    }

    return await response.json();
};

export const registrarFormulaPresupuestada = async (datosPresupuesto) => {
    const response = await fetch(`${API_URL}/formulas/presupuestar`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(datosPresupuesto),
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudo registrar el presupuesto de la fórmula"));
    }

    return await response.json();
};

export const registrarRecetaMedica = async (datosReceta) => {
    const response = await fetch(`${API_URL}/formulas/recetas`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(datosReceta),
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudo registrar la receta médica"));
    }

    return await response.json();
};

export const buscarClientePorDocumento = async (documento) => {
    const response = await fetch(`${API_URL}/clientes/documento/${documento}`, {
        method: "GET",
        headers: getAuthHeaders(),
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "Cliente no encontrado en la base de datos"));
    }

    return await response.json();
};

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

    const response = await fetch(`${API_URL}/clientes`, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        throw new Error(await parseError(response, "No se pudo registrar al nuevo cliente"));
    }

    return await response.json();
};
