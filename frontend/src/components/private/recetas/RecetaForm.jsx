import React, { useState } from "react";
import "./recetaForm.css";

import {
    buscarClientePorDocumento,
    registrarCliente
} from "../../../services/recetas/recetaService";

const ModalRegistrarCliente = ({ dniInicial, onGuardar, onCancelar }) => {
    const [datos, setDatos] = useState({
        documento: dniInicial || "",
        tipoCliente: "Natural",
        nombres: "",
        apellidos: "",
        telefono: "",
        whatsapp: "",
        email: "",
        direccion: "",
    });
    const [guardando, setGuardando] = useState(false);
    const [error, setError] = useState("");

    const handleChange = (e) => setDatos({ ...datos, [e.target.name]: e.target.value });

    const handleGuardar = async () => {
        if (!datos.nombres.trim() || !datos.apellidos.trim()) {
            setError("Nombres y Apellidos son obligatorios.");
            return;
        }
        setError("");
        setGuardando(true);
        try {
            const clienteGuardado = await registrarCliente(datos);
            onGuardar(clienteGuardado);
        } catch {
            setError("No se pudo registrar el cliente. Intente nuevamente.");
        } finally {
            setGuardando(false);
        }
    };

    return (
        <div className="modal-overlay">
            <div className="modal-card">
                <div className="modal-header">
                    <div>
                        <h3>Registrar cliente nuevo</h3>
                        <p>Completa los datos del cliente para usarlo en la receta.</p>
                    </div>
                    <button className="modal-close-btn" onClick={onCancelar}>✕</button>
                </div>
                <div className="modal-body">
                    <div className="modal-row">
                        <div className="modal-field">
                            <label>DNI / RUC</label>
                            <input type="text" name="documento" placeholder="Ejemplo: 76543210" value={datos.documento} onChange={handleChange} maxLength="11" />
                        </div>
                        <div className="modal-field">
                            <label>Tipo cliente</label>
                            <select name="tipoCliente" value={datos.tipoCliente} onChange={handleChange}>
                                <option value="Natural">Natural</option>
                                <option value="Empresa">Empresa</option>
                            </select>
                        </div>
                    </div>
                    <div className="modal-row">
                        <div className="modal-field">
                            <label>Nombres</label>
                            <input type="text" name="nombres" placeholder="Nombres" value={datos.nombres} onChange={handleChange} />
                        </div>
                        <div className="modal-field">
                            <label>Apellidos</label>
                            <input type="text" name="apellidos" placeholder="Apellidos" value={datos.apellidos} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="modal-row">
                        <div className="modal-field">
                            <label>Teléfono</label>
                            <input type="text" name="telefono" placeholder="Teléfono" value={datos.telefono} onChange={handleChange} />
                        </div>
                        <div className="modal-field">
                            <label>WhatsApp</label>
                            <input type="text" name="whatsapp" placeholder="WhatsApp" value={datos.whatsapp} onChange={handleChange} />
                        </div>
                    </div>
                    <div className="modal-row">
                        <div className="modal-field">
                            <label>Email</label>
                            <input type="email" name="email" placeholder="correo@ejemplo.com" value={datos.email} onChange={handleChange} />
                        </div>
                        <div className="modal-field">
                            <label>Dirección</label>
                            <input type="text" name="direccion" placeholder="Dirección" value={datos.direccion} onChange={handleChange} />
                        </div>
                    </div>
                    {error && <span className="error-text block-error">{error}</span>}
                </div>
                <div className="modal-footer">
                    <button className="btn-cancel-flow" onClick={onCancelar}>Cancelar</button>
                    <button className="btn-submit-flow" onClick={handleGuardar} disabled={guardando}>
                        {guardando ? "Guardando..." : "Guardar cliente"}
                    </button>
                </div>
            </div>
        </div>
    );
};

const RecetaForm = ({ onValidacionExitosa, onCancelar, formulaSolicitadaInsumos = [] }) => {
    const [formData, setFormData] = useState({
        dniPaciente: "",
        nombrePaciente: "",
        medicoPrescriptor: "",
        diagnostico: "",
        contraindicaciones: "",
        tipoFormula: ""
    });
    const [componentes, setComponentes] = useState([
        { nombre_insumo: "", cantidad_usada: "", unidad_medida: "mg" }
    ]);
    const [errores, setErrores] = useState({});
    const [buscandoCliente, setBuscandoCliente] = useState(false);
    const [mostrarModal, setMostrarModal] = useState(false);
    const [mensajeBusqueda, setMensajeBusqueda] = useState({ tipo: "", texto: "" });

    const handleChange = (e) => {
        const { name, value } = e.target;
        // El nombre del paciente no es editable manualmente
        if (name === "nombrePaciente") return;
        setFormData({ ...formData, [name]: value });
    };

    const handleBuscarCliente = async () => {
        // Si está vacío, avisar que ingrese un DNI
        if (!formData.dniPaciente.trim()) {
            setMensajeBusqueda({ tipo: "error", texto: "Ingrese un DNI para buscar." });
            return;
        }
        if (!/^\d{8}$/.test(formData.dniPaciente)) {
            setMensajeBusqueda({ tipo: "error", texto: "El DNI debe tener exactamente 8 dígitos." });
            return;
        }
        setBuscandoCliente(true);
        setMensajeBusqueda({ tipo: "", texto: "" });
        try {
            const cliente = await buscarClientePorDocumento(formData.dniPaciente);
            const nombreCompleto = `${cliente.nombres || ""} ${cliente.apellidos || ""}`.trim();
            setFormData(prev => ({ ...prev, nombrePaciente: nombreCompleto }));
            setMensajeBusqueda({ tipo: "exito", texto: `Cliente encontrado: ${nombreCompleto}` });
        } catch {
            // No existe → mostrar mensaje y opción de registrar
            setMensajeBusqueda({ tipo: "error", texto: "DNI no encontrado. Registre el cliente." });
        } finally {
            setBuscandoCliente(false);
        }
    };

    const handleKeyDownDni = (e) => {
        if (e.key === "Enter") { e.preventDefault(); handleBuscarCliente(); }
    };

    const handleClienteGuardado = (clienteGuardado) => {
        const nombreCompleto = `${clienteGuardado.nombres || ""} ${clienteGuardado.apellidos || ""}`.trim();
        setFormData(prev => ({ ...prev, nombrePaciente: nombreCompleto }));
        setMensajeBusqueda({ tipo: "exito", texto: `Cliente registrado: ${nombreCompleto}` });
        setMostrarModal(false);
    };

    const handleAgregarComponente = () => {
        setComponentes([...componentes, { nombre_insumo: "", cantidad_usada: "", unidad_medida: "mg" }]);
    };

    const handleEliminarComponente = (index) => {
        if (componentes.length === 1) return;
        setComponentes(componentes.filter((_, i) => i !== index));
    };

    const handleComponenteChange = (index, campo, valor) => {
        const nuevosComponentes = [...componentes];
        nuevosComponentes[index][campo] = valor;
        setComponentes(nuevosComponentes);
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const nuevosErrores = {};
        if (!/^\d{8}$/.test(formData.dniPaciente)) nuevosErrores.dniPaciente = "El DNI debe contener exactamente 8 dígitos numéricos.";
        if (!formData.nombrePaciente.trim()) nuevosErrores.nombrePaciente = "Debe buscar o registrar un paciente primero.";
        if (!formData.medicoPrescriptor.trim()) nuevosErrores.medicoPrescriptor = "El nombre del médico es obligatorio.";
        if (!formData.diagnostico) nuevosErrores.diagnostico = "Debe seleccionar un diagnóstico clínico.";
        if (!formData.tipoFormula) nuevosErrores.tipoFormula = "Debe seleccionar el tipo de fórmula magistral.";
        const componentesValidos = componentes.filter(c => c.nombre_insumo.trim() && c.cantidad_usada);
        if (componentesValidos.length === 0) nuevosErrores.componentes = "Debe escribir al menos un insumo válido para la fórmula.";
        if (Object.keys(nuevosErrores).length > 0) { setErrores(nuevosErrores); return; }
        setErrores({});
        onValidacionExitosa({ ...formData, componentes: componentesValidos });
    };

    return (
        <>
            {mostrarModal && (
                <ModalRegistrarCliente
                    dniInicial={formData.dniPaciente}
                    onGuardar={handleClienteGuardado}
                    onCancelar={() => setMostrarModal(false)}
                />
            )}
            <div className="receta-form-container">
                <div className="receta-form-header">
                    <div>
                        <h2>Registro de Receta Médica</h2>
                        <p>Ingrese los datos de la prescripción para verificar su legitimidad antes de proceder al laboratorio.</p>
                    </div>
                    <button
                        type="button"
                        className="btn-registrar-cliente-header"
                        onClick={() => setMostrarModal(true)}
                    >
                        + Registrar cliente
                    </button>
                </div>

                <form onSubmit={handleSubmit} className="receta-blueprint-form">
                    <div className="form-row-twin">
                        <div className="form-group">
                            <label>DNI del Paciente</label>
                            <div className="input-with-action">
                                <input
                                    type="text"
                                    name="dniPaciente"
                                    placeholder="Ej. 74859612"
                                    maxLength="8"
                                    value={formData.dniPaciente}
                                    onChange={handleChange}
                                    onKeyDown={handleKeyDownDni}
                                />
                                <button type="button" className="btn-buscar-cliente" onClick={handleBuscarCliente} disabled={buscandoCliente}>
                                    {buscandoCliente ? "Buscando..." : "Buscar"}
                                </button>
                            </div>
                            {errores.dniPaciente && <span className="error-text">{errores.dniPaciente}</span>}
                            {mensajeBusqueda.tipo === "error" && mensajeBusqueda.texto && (
                                <div className="busqueda-feedback-box busqueda-error">
                                    <span>✗ {mensajeBusqueda.texto}</span>
                                </div>
                            )}
                        </div>
                        <div className="form-group">
                            <label>Nombre Completo del Paciente</label>
                            {/* readOnly: se autocompleta solo, no editable */}
                            <input
                                type="text"
                                name="nombrePaciente"
                                placeholder="Se completará al buscar"
                                value={formData.nombrePaciente}
                                readOnly
                                className="input-readonly"
                            />
                            {errores.nombrePaciente && <span className="error-text">{errores.nombrePaciente}</span>}
                        </div>
                    </div>

                    <div className="form-row-twin">
                        <div className="form-group">
                            <label>Médico Prescriptor</label>
                            <input type="text" name="medicoPrescriptor" placeholder="Ej. Dr. Carlos Mendoza" value={formData.medicoPrescriptor} onChange={handleChange} />
                            {errores.medicoPrescriptor && <span className="error-text">{errores.medicoPrescriptor}</span>}
                        </div>
                        <div className="form-group">
                            <label>Contraindicaciones</label>
                            <select name="contraindicaciones" value={formData.contraindicaciones} onChange={handleChange}>
                                <option value="">-- Seleccione una opción --</option>
                                <option value="Sin contraindicaciones reportadas">Sin contraindicaciones reportadas</option>
                                <option value="Evitar exposicion solar">Evitar exposicion solar</option>
                                <option value="Paciente refiere alergia leve">Paciente refiere alergia leve</option>
                            </select>
                        </div>
                    </div>

                    <div className="form-row-full">
                        <div className="form-group">
                            <label>Diagnóstico Clínico</label>
                            <select name="diagnostico" value={formData.diagnostico} onChange={handleChange}>
                                <option value="">-- Seleccione un diagnóstico médico --</option>
                                <option value="Dermatitis atópica severa generalizada">Dermatitis atópica severa generalizada</option>
                                <option value="Alopecia androgénica en tratamiento">Alopecia androgénica en tratamiento</option>
                                <option value="Acné vulgar moderado">Acné vulgar moderado</option>
                                <option value="Rosácea eritematotelangiectásica">Rosácea eritematotelangiectásica</option>
                            </select>
                            {errores.diagnostico && <span className="error-text">{errores.diagnostico}</span>}
                        </div>
                    </div>

                    <div className="form-row-full">
                        <div className="form-group">
                            <label>Descripción Receta (Tipo de Fórmula)</label>
                            <select name="tipoFormula" value={formData.tipoFormula} onChange={handleChange}>
                                <option value="">-- Seleccione el tipo de preparado --</option>
                                <option value="Crema dermatologica magistral">Crema dermatologica magistral</option>
                                <option value="Locion antiseptica magistral">Locion antiseptica magistral</option>
                                <option value="Solucion topica magistral">Solucion topica magistral</option>
                            </select>
                            {errores.tipoFormula && <span className="error-text">{errores.tipoFormula}</span>}
                        </div>
                    </div>

                    <div className="form-row-full formula-solicitada-badge-container">
                        <strong>Fórmula Solicitada:</strong>
                        <div className="formula-solicitada-string">
                            {componentes.filter(c => c.nombre_insumo.trim()).length > 0 ? (
                                componentes.filter(c => c.nombre_insumo.trim())
                                    .map((c) => `${c.nombre_insumo} (${c.cantidad_usada || 0} ${c.unidad_medida})`)
                                    .join(", ")
                            ) : (
                                <span className="text-placeholder-inline">No se han ingresado insumos químicos todavía...</span>
                            )}
                        </div>
                    </div>

                    <div className="form-row-full componentes-dinamicos-wrapper">
                        <div className="componentes-dinamicos-header">
                            <label>Componentes y Proporciones Básicas</label>
                            <button type="button" className="btn-add-insumo-row" onClick={handleAgregarComponente}>+ Agregar Componente</button>
                        </div>
                        <div className="tabla-dinamica-scroll">
                            <table className="tabla-insumos-blueprint">
                                <thead>
                                    <tr>
                                        <th>Insumo / Producto Químico</th>
                                        <th style={{ width: "160px" }}>Cantidad Usada</th>
                                        <th style={{ width: "140px" }}>Unidad Medida</th>
                                        <th style={{ width: "60px", textAlign: "center" }}>Quitar</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {componentes.map((componente, index) => (
                                        <tr key={index}>
                                            <td>
                                                <input type="text" placeholder="Escriba el nombre del insumo libremente..." value={componente.nombre_insumo} onChange={(e) => handleComponenteChange(index, "nombre_insumo", e.target.value)} className="input-insumo-libre" />
                                            </td>
                                            <td>
                                                <input type="number" step="0.01" min="0.01" placeholder="0.00" value={componente.cantidad_usada} onChange={(e) => handleComponenteChange(index, "cantidad_usada", e.target.value)} />
                                            </td>
                                            <td>
                                                <select value={componente.unidad_medida} onChange={(e) => handleComponenteChange(index, "unidad_medida", e.target.value)} className="select-unidad-inline">
                                                    <option value="mg">mg</option>
                                                    <option value="g">g</option>
                                                    <option value="ml">ml</option>
                                                </select>
                                            </td>
                                            <td style={{ textAlign: "center" }}>
                                                <button type="button" className="btn-remove-insumo-row" onClick={() => handleEliminarComponente(index)} disabled={componentes.length === 1}>🗑️</button>
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        {errores.componentes && <span className="error-text block-error">{errores.componentes}</span>}
                    </div>

                    <div className="form-actions-row">
                        <button type="button" className="btn-cancel-flow" onClick={onCancelar}>Cancelar</button>
                        <button type="submit" className="btn-submit-flow">Validar Receta y Costear Insumos</button>
                    </div>
                </form>
            </div>
        </>
    );
};

export default RecetaForm;