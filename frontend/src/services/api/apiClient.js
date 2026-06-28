import { API_URL } from "../../config/api";

let isRefreshing = false;
let pendingRequests = [];

const resolvePending = (newToken) =>
    pendingRequests.forEach(({ resolve }) => resolve(newToken));

const rejectPending = (error) =>
    pendingRequests.forEach(({ reject }) => reject(error));

const clearPending = () => {
    pendingRequests = [];
};

// Limpiar sesión y redirigir al login 
const forceLogout = () => {
    [
        "token",
        "refreshToken",
        "usuario",
        "rol",
        "nombres",
        "apellidos",
        "idCuenta",
        "idTrabajador",
        "permisos",
    ].forEach((key) => localStorage.removeItem(key));

    window.location.replace("/login");
};

// Parsear error del servidor 
const parseError = async (response, mensajeError) => {
    const text = await response.text().catch(() => "");
    if (!text) return mensajeError || "No se pudo completar la operación";
    try {
        const json = JSON.parse(text);
        return json.message || json.error || mensajeError || text;
    } catch {
        return text || mensajeError || "No se pudo completar la operación";
    }
};


// Resolver respuesta según tipo esperado 
const resolveResponse = (response, responseType) => {
    if (response.status === 204) return null;
    if (responseType === "blob") return response.blob();
    return response.json();
};

// Construir y ejecutar la petición fetch
const doRequest = (path, options, token) => {
    const { responseType, mensajeError, headers: extraHeaders = {}, ...rest } = options;

    return fetch(`${API_URL}${path}`, {
        ...rest,
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...extraHeaders,
        },
    });
};

// Cliente HTTP principal
const apiClient = async (path, options = {}) => {
    const { responseType = "json", mensajeError } = options;
    const token = localStorage.getItem("token");

    const response = await doRequest(path, options, token);

    //Respuesta exitosa o error normal (no 401) 
    if (response.status !== 401) {
        if (!response.ok) throw new Error(await parseError(response, mensajeError));
        return resolveResponse(response, responseType);
    }

    //  401: access token expirado
    // Si ya hay un refresco en curso, encolar y esperar
    if (isRefreshing) {
        const newToken = await new Promise((resolve, reject) =>
            pendingRequests.push({ resolve, reject })
        );
        const retried = await doRequest(path, options, newToken);
        if (!retried.ok) throw new Error(await parseError(retried, mensajeError));
        return resolveResponse(retried, responseType);
    }

    // Soy el primer 401 — inicio el proceso de refresco
    isRefreshing = true;

    try {
        const refreshToken = localStorage.getItem("refreshToken");

        if (!refreshToken) throw new Error("Sin refresh token");

        // Llamar al endpoint de refresco
        const refreshResponse = await fetch(`${API_URL}/auth/refresh`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ refreshToken }),
        });

        if (!refreshResponse.ok) throw new Error("Refresh token expirado");

        const { accessToken: newToken } = await refreshResponse.json();

        // Guardar nuevo access token
        localStorage.setItem("token", newToken);

        // Desbloquear peticiones en cola con el nuevo token
        resolvePending(newToken);

        // Reintentar la petición original con el nuevo token
        const retried = await doRequest(path, options, newToken);
        if (!retried.ok) throw new Error(await parseError(retried, mensajeError));
        return resolveResponse(retried, responseType);

    } catch (error) {
        rejectPending(error);
        forceLogout();
        throw error;
    } finally {
        clearPending();
        isRefreshing = false;
    }
};

export default apiClient;
