import apiClient from "../api/apiClient";

export const obtenerRecetas = () =>
    apiClient("/formulas/recetas-listas", {
        mensajeError: "No se pudieron obtener las recetas magistrales",
    });

export const obtenerInsumosDisponibles = () =>
    apiClient("/formulas/insumos", {
        mensajeError: "No se pudieron obtener los insumos disponibles",
    });

export const registrarFormulaPresupuestada = (datosPresupuesto) =>
    apiClient("/formulas/presupuestar", {
        method: "POST",
        body: JSON.stringify(datosPresupuesto),
        mensajeError: "No se pudo registrar el presupuesto de la fórmula",
    });

export const registrarRecetaMedica = (datosReceta) =>
    apiClient("/formulas/recetas", {
        method: "POST",
        body: JSON.stringify(datosReceta),
        mensajeError: "No se pudo registrar la receta médica",
    });

export const cambiarEstadoReceta = (idReceta, estado, observacion = "") =>
    apiClient(`/formulas/recetas/${idReceta}/estado`, {
        method: "PATCH",
        body: JSON.stringify({ estado, observacion }),
        mensajeError: "No se pudo actualizar el estado de la receta",
    });

export const buscarClientePorDocumento = (documento) =>
    apiClient(`/clientes/documento/${documento}`, {
        mensajeError: "Cliente no encontrado en la base de datos",
    });

export const registrarCliente = (datosCliente) =>
    apiClient("/clientes", {
        method: "POST",
        body: JSON.stringify({
            dniRuc:      datosCliente.dniRuc || datosCliente.documento,
            tipoCliente: datosCliente.tipoCliente || "Natural",
            nombres:     datosCliente.nombres,
            apellidos:   datosCliente.apellidos,
            telefono:    datosCliente.telefono,
            whatsapp:    datosCliente.whatsapp,
            email:       datosCliente.email,
            direccion:   datosCliente.direccion,
        }),
        mensajeError: "No se pudo registrar al nuevo cliente",
    });
