import { useEffect, useMemo, useState } from "react";
import {
    actualizarEstadoAccionGerencial,
    listarAccionesGerenciales,
    listarInformesGerenciales,
    obtenerResumenReportes,
    registrarAccionGerencial,
    registrarInformeGerencial,
} from "../../services/reportes/reporteService";
import "./reporte.css";

const formatearMoneda = (valor) =>
    new Intl.NumberFormat("es-PE", {
        style: "currency",
        currency: "PEN",
        minimumFractionDigits: 2,
    }).format(Number(valor || 0));

const formatearNumero = (valor) =>
    new Intl.NumberFormat("es-PE", {
        maximumFractionDigits: 0,
    }).format(Number(valor || 0));

const formatearValor = (item) => {
    if (item?.formato === "MONEDA") {
        return formatearMoneda(item.valor);
    }

    return formatearNumero(item?.valor);
};

const formatearFecha = (fecha) => {
    if (!fecha) return "-";

    return new Intl.DateTimeFormat("es-PE", {
        dateStyle: "short",
        timeStyle: "short",
    }).format(new Date(fecha));
};

const obtenerMaximo = (items = []) => {
    const valores = items.map((item) => Number(item.valor || 0));
    return Math.max(...valores, 1);
};

const Reporte = () => {
    const [resumen, setResumen] = useState(null);
    const [informes, setInformes] = useState([]);
    const [acciones, setAcciones] = useState([]);
    const [loading, setLoading] = useState(true);
    const [guardando, setGuardando] = useState(false);
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");

    const [informeForm, setInformeForm] = useState({
        area: "Ventas",
        titulo: "",
        descripcion: "",
    });

    const [accionForm, setAccionForm] = useState({
        idInforme: "",
        accionTomar: "",
    });

    const kpisPrincipales = useMemo(() => resumen?.kpis || [], [resumen]);
    const maxVentasCanal = obtenerMaximo(resumen?.ventasPorCanal || []);
    const maxOrdenesEstado = obtenerMaximo(resumen?.ordenesPorEstado || []);
    const maxPagosMetodo = obtenerMaximo(resumen?.pagosPorMetodo || []);
    const maxVentasDias = obtenerMaximo(resumen?.ventasUltimosDias || []);

    useEffect(() => {
        cargarDatos();
    }, []);

    const cargarDatos = async () => {
        try {
            setLoading(true);
            setError("");

            const [resumenData, informesData, accionesData] = await Promise.all([
                obtenerResumenReportes(),
                listarInformesGerenciales(),
                listarAccionesGerenciales(),
            ]);

            setResumen(resumenData);
            setInformes(informesData);
            setAcciones(accionesData);

            if (!accionForm.idInforme && informesData.length > 0) {
                setAccionForm((prev) => ({
                    ...prev,
                    idInforme: String(informesData[0].idInforme),
                }));
            }
        } catch (err) {
            setError(err.message || "No se pudieron cargar los reportes");
        } finally {
            setLoading(false);
        }
    };

    const handleInformeChange = (event) => {
        const { name, value } = event.target;
        setInformeForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleAccionChange = (event) => {
        const { name, value } = event.target;
        setAccionForm((prev) => ({ ...prev, [name]: value }));
    };

    const handleRegistrarInforme = async (event) => {
        event.preventDefault();

        if (!informeForm.area || !informeForm.titulo) {
            setError("Completa el área y el título del informe");
            return;
        }

        try {
            setGuardando(true);
            setError("");
            setMensaje("");

            await registrarInformeGerencial(informeForm);
            setInformeForm({ area: "Ventas", titulo: "", descripcion: "" });
            setMensaje("Informe registrado correctamente");
            await cargarDatos();
        } catch (err) {
            setError(err.message || "No se pudo registrar el informe");
        } finally {
            setGuardando(false);
        }
    };

    const handleRegistrarAccion = async (event) => {
        event.preventDefault();

        if (!accionForm.idInforme || !accionForm.accionTomar) {
            setError("Selecciona un informe y escribe la acción gerencial");
            return;
        }

        try {
            setGuardando(true);
            setError("");
            setMensaje("");

            await registrarAccionGerencial({
                idInforme: Number(accionForm.idInforme),
                accionTomar: accionForm.accionTomar,
            });
            setAccionForm((prev) => ({ ...prev, accionTomar: "" }));
            setMensaje("Acción gerencial registrada correctamente");
            await cargarDatos();
        } catch (err) {
            setError(err.message || "No se pudo registrar la acción");
        } finally {
            setGuardando(false);
        }
    };

    const handleCambiarEstadoAccion = async (idAccion, estado) => {
        try {
            setError("");
            setMensaje("");

            await actualizarEstadoAccionGerencial(idAccion, estado);
            setMensaje("Estado de la acción actualizado");
            await cargarDatos();
        } catch (err) {
            setError(err.message || "No se pudo actualizar la acción");
        }
    };

    const renderSerie = (titulo, subtitulo, items = [], maximo, tipo = "NUMERO") => (
        <section className="report-card report-chart-card">
            <div className="report-card-header">
                <div>
                    <h2>{titulo}</h2>
                    <span>{subtitulo}</span>
                </div>
            </div>

            <div className="report-bars">
                {items.length === 0 ? (
                    <p className="report-empty">No hay datos disponibles</p>
                ) : (
                    items.map((item) => {
                        const ancho = Math.max((Number(item.valor || 0) / maximo) * 100, 5);
                        return (
                            <div className="report-bar-row" key={`${titulo}-${item.etiqueta}`}>
                                <div className="report-bar-label">
                                    <span>{item.etiqueta || "Sin dato"}</span>
                                    <strong>
                                        {tipo === "MONEDA"
                                            ? formatearMoneda(item.valor)
                                            : formatearNumero(item.valor)}
                                    </strong>
                                </div>
                                <div className="report-bar-track">
                                    <div
                                        className="report-bar-fill"
                                        style={{ width: `${ancho}%` }}
                                    />
                                </div>
                            </div>
                        );
                    })
                )}
            </div>
        </section>
    );

    if (loading) {
        return (
            <div className="report-page">
                <div className="report-header">
                    <h1>Reportes y toma de decisiones</h1>
                    <p>Cargando indicadores gerenciales...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="report-page">
            <div className="report-header">
                <div>
                    <h1>Reportes y toma de decisiones</h1>
                    <p>
                        Consolida indicadores de ventas, caja, almacén, compras, despacho y recetas magistrales.
                    </p>
                </div>
                <button className="report-refresh" onClick={cargarDatos}>
                    Actualizar
                </button>
            </div>

            {error && <div className="report-alert error">{error}</div>}
            {mensaje && <div className="report-alert success">{mensaje}</div>}

            <section className="report-kpis">
                {kpisPrincipales.map((item) => (
                    <article className="report-kpi" key={item.codigo}>
                        <span>{item.titulo}</span>
                        <strong>{formatearValor(item)}</strong>
                        <p>{item.descripcion}</p>
                    </article>
                ))}
            </section>

            <div className="report-grid">
                {renderSerie(
                    "Órdenes por estado",
                    "Seguimiento del flujo venta-caja-despacho",
                    resumen?.ordenesPorEstado || [],
                    maxOrdenesEstado
                )}
                {renderSerie(
                    "Ventas por canal",
                    "Presencial, teléfono, WhatsApp y otros canales",
                    resumen?.ventasPorCanal || [],
                    maxVentasCanal
                )}
                {renderSerie(
                    "Cobros por método de pago",
                    "Monto cobrado por caja según método",
                    resumen?.pagosPorMetodo || [],
                    maxPagosMetodo,
                    "MONEDA"
                )}
                {renderSerie(
                    "Ventas últimos 7 días",
                    "Evolución reciente de ventas válidas",
                    resumen?.ventasUltimosDias || [],
                    maxVentasDias,
                    "MONEDA"
                )}
            </div>

            <section className="report-card">
                <div className="report-card-header">
                    <div>
                        <h2>Productos críticos</h2>
                        <span>Stock bajo o productos próximos a vencer</span>
                    </div>
                </div>

                <div className="report-table-wrapper">
                    <table className="report-table">
                        <thead>
                            <tr>
                                <th>Producto</th>
                                <th>Stock actual</th>
                                <th>Stock mínimo</th>
                                <th>Vencimiento</th>
                            </tr>
                        </thead>
                        <tbody>
                            {(resumen?.stockCritico || []).length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="report-empty-cell">
                                        No hay productos críticos registrados
                                    </td>
                                </tr>
                            ) : (
                                resumen.stockCritico.map((producto) => (
                                    <tr key={producto.idProducto}>
                                        <td>{producto.producto}</td>
                                        <td>{producto.stockActual}</td>
                                        <td>{producto.stockMinimo}</td>
                                        <td>{producto.fechaVencimiento || "-"}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </section>

            <div className="report-forms-grid">
                <section className="report-card">
                    <div className="report-card-header">
                        <div>
                            <h2>Registrar informe</h2>
                            <span>Informe enviado por un área para gerencia</span>
                        </div>
                    </div>

                    <form className="report-form" onSubmit={handleRegistrarInforme}>
                        <label>
                            Área
                            <select name="area" value={informeForm.area} onChange={handleInformeChange}>
                                <option value="Ventas">Ventas</option>
                                <option value="Caja">Caja</option>
                                <option value="Almacén">Almacén</option>
                                <option value="Compras">Compras</option>
                                <option value="Despacho">Despacho</option>
                                <option value="Recetas Magistrales">Recetas Magistrales</option>
                                <option value="Administración">Administración</option>
                            </select>
                        </label>

                        <label>
                            Título
                            <input
                                name="titulo"
                                value={informeForm.titulo}
                                onChange={handleInformeChange}
                                placeholder="Ej. Informe semanal de ventas"
                            />
                        </label>

                        <label>
                            Descripción
                            <textarea
                                name="descripcion"
                                value={informeForm.descripcion}
                                onChange={handleInformeChange}
                                placeholder="Describe el hallazgo, problema o resumen del área"
                                rows="4"
                            />
                        </label>

                        <button disabled={guardando} type="submit">
                            {guardando ? "Guardando..." : "Registrar informe"}
                        </button>
                    </form>
                </section>

                <section className="report-card">
                    <div className="report-card-header">
                        <div>
                            <h2>Registrar acción gerencial</h2>
                            <span>Acción a tomar a partir de un informe</span>
                        </div>
                    </div>

                    <form className="report-form" onSubmit={handleRegistrarAccion}>
                        <label>
                            Informe base
                            <select
                                name="idInforme"
                                value={accionForm.idInforme}
                                onChange={handleAccionChange}
                            >
                                <option value="">Selecciona un informe</option>
                                {informes.map((informe) => (
                                    <option key={informe.idInforme} value={informe.idInforme}>
                                        {informe.area} - {informe.titulo}
                                    </option>
                                ))}
                            </select>
                        </label>

                        <label>
                            Acción a tomar
                            <textarea
                                name="accionTomar"
                                value={accionForm.accionTomar}
                                onChange={handleAccionChange}
                                placeholder="Ej. Priorizar compra preventiva de productos críticos"
                                rows="5"
                            />
                        </label>

                        <button disabled={guardando} type="submit">
                            {guardando ? "Guardando..." : "Registrar acción"}
                        </button>
                    </form>
                </section>
            </div>

            <section className="report-card">
                <div className="report-card-header">
                    <div>
                        <h2>Informes recibidos</h2>
                        <span>Historial de informes enviados por las áreas</span>
                    </div>
                </div>

                <div className="report-table-wrapper">
                    <table className="report-table">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Área</th>
                                <th>Título</th>
                                <th>Responsable</th>
                                <th>Descripción</th>
                            </tr>
                        </thead>
                        <tbody>
                            {informes.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="report-empty-cell">
                                        No hay informes registrados
                                    </td>
                                </tr>
                            ) : (
                                informes.map((informe) => (
                                    <tr key={informe.idInforme}>
                                        <td>{formatearFecha(informe.fechaEmision)}</td>
                                        <td>{informe.area}</td>
                                        <td>{informe.titulo}</td>
                                        <td>{informe.trabajador}</td>
                                        <td>{informe.descripcion || "-"}</td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </section>

            <section className="report-card">
                <div className="report-card-header">
                    <div>
                        <h2>Acciones gerenciales</h2>
                        <span>Seguimiento de decisiones y acciones a tomar</span>
                    </div>
                </div>

                <div className="report-table-wrapper">
                    <table className="report-table">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Informe</th>
                                <th>Acción</th>
                                <th>Gerente</th>
                                <th>Estado</th>
                                <th>Cambiar estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            {acciones.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="report-empty-cell">
                                        No hay acciones gerenciales registradas
                                    </td>
                                </tr>
                            ) : (
                                acciones.map((accion) => (
                                    <tr key={accion.idAccion}>
                                        <td>{formatearFecha(accion.fechaRegistro)}</td>
                                        <td>
                                            <strong>{accion.area}</strong>
                                            <span>{accion.informe}</span>
                                        </td>
                                        <td>{accion.accionTomar}</td>
                                        <td>{accion.gerente}</td>
                                        <td>
                                            <span className={`report-status ${accion.estado?.replaceAll(" ", "-").toLowerCase()}`}>
                                                {accion.estado}
                                            </span>
                                        </td>
                                        <td>
                                            <select
                                                className="report-status-select"
                                                value={accion.estado}
                                                onChange={(e) => handleCambiarEstadoAccion(accion.idAccion, e.target.value)}
                                            >
                                                <option value="Pendiente">Pendiente</option>
                                                <option value="En Proceso">En Proceso</option>
                                                <option value="Completada">Completada</option>
                                                <option value="Cancelada">Cancelada</option>
                                            </select>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
};

export default Reporte;
