import { API_URL } from "../../config/api";

export const obtenerUsuarios = async () => {
    const token = localStorage.getItem("token");

    const headers = {
        Authorization: `Bearer ${token}`,
    };

    const [trabajadoresResponse, cuentasResponse] = await Promise.all([
        fetch(`${API_URL}/trabajadores`, { headers }),
        fetch(`${API_URL}/usuarios`, { headers }),
    ]);

    if (!trabajadoresResponse.ok || !cuentasResponse.ok) {
        throw new Error("Error al obtener usuarios");
    }

    const [trabajadores, cuentas] = await Promise.all([
        trabajadoresResponse.json(),
        cuentasResponse.json(),
    ]);

    const cuentasPorTrabajador = new Map(
        cuentas.map((cuenta) => [cuenta.idTrabajador, cuenta])
    );

    return trabajadores.map((trabajador) => ({
        ...trabajador,
        ...cuentasPorTrabajador.get(trabajador.idTrabajador),
        estado: trabajador.estado,
    }));
};

export const crearUsuario = async (usuarioRequest) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/usuarios`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(usuarioRequest),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo registrar el usuario");
    }

    return await response.json();
};

export const actualizarTrabajador = async (idTrabajador, trabajadorRequest) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/trabajadores/${idTrabajador}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(trabajadorRequest),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo actualizar el trabajador");
    }

    return await response.json();
};

export const actualizarEstadoTrabajador = async (idTrabajador, estado) => {
    const token = localStorage.getItem("token");

    const response = await fetch(`${API_URL}/trabajadores/${idTrabajador}/estado`, {
        method: "PATCH",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
            estado,
        }),
    });

    if (!response.ok) {
        const errorText = await response.text();
        throw new Error(errorText || "No se pudo actualizar el estado del trabajador");
    }

    return await response.json();
};
