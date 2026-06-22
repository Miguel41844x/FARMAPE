import { useEffect, useMemo, useState } from "react";
import { FiActivity, FiDownload, FiFilter, FiPlusCircle, FiRefreshCcw } from "react-icons/fi";
import FeedbackMessage from "../../../components/common/FeedbackMessage";
import EmptyState from "../../../components/common/EmptyState";
import LoadingState from "../../../components/common/LoadingState";
import { useAuth } from "../../../context/AuthContext";
import { PERMISSIONS } from "../../../constants/permissions";
import {
    exportarEventosAuditoria,
    listarEventosAuditoria,
    obtenerResumenAuditoria,
    registrarEventoAuditoria,
} from "../../../services/auditoria/auditoriaService";
import "./auditoria.css";

const filtrosIniciales = {
    modulo: "",
    accion: "",
    severidad: "",
    usuario: "",
    tipoEvento: "",
    desde: "",
    hasta: "",
    q: "",
    limite: 100,
};

const observacionInicial = {
    modulo: "General",
    entidad: "Observación manual",
    entidadId: "",
    accion: "OBSERVACION",
    descripcion: "",
    valorAnterior: "",
    valorNuevo: "",
    severidad: "INFO",
};

const formatoFecha = (fecha) => {
    if (!fecha) return "-";
    return new Intl.DateTimeFormat("es-PE", { dateStyle: "short", timeStyle: "short" }).format(new Date(fecha));
};

const severidadClass = (severidad) => `audit-badge audit-${(severidad || "info").toLowerCase()}`;

const Auditoria = () => {
    const { hasPermission } = useAuth();
    const puedeRegistrar = hasPermission(PERMISSIONS.AUDIT_MANAGE);
    const [resumen, setResumen] = useState(null);
    const [eventos, setEventos] = useState([]);
    const [filtros, setFiltros] = useState(filtrosIniciales);
    const [observacion, setObservacion] = useState(observacionInicial);
    const [loading, setLoading] = useState(false);
    const [feedback, setFeedback] = useState(null);
    const [mostrarFormulario, setMostrarFormulario] = useState(false);

    const modulosDisponibles = useMemo(() => {
        const base = ["Ventas", "Compras", "Almacén", "Inventario", "Caja", "Usuarios", "Roles", "Recetas", "Reportes", "Seguridad", "General"];
        const dinamicos = eventos.map((evento) => evento.modulo).filter(Boolean);
        return [...new Set([...base, ...dinamicos])];
    }, [eventos]);

    const cargarDatos = async (filtrosAplicados = filtros) => {
        setLoading(true);
        setFeedback(null);
        try {
            const [resumenData, eventosData] = await Promise.all([
                obtenerResumenAuditoria(),
                listarEventosAuditoria(filtrosAplicados),
            ]);
            setResumen(resumenData);
            setEventos(Array.isArray(eventosData) ? eventosData : []);
        } catch (err) {
            setFeedback({ type: "error", message: err.message || "No se pudo cargar auditoría" });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarDatos(filtrosIniciales);
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleFiltroChange = (event) => {
        const { name, value } = event.target;
        setFiltros((current) => ({ ...current, [name]: value }));
    };

    const handleObservacionChange = (event) => {
        const { name, value } = event.target;
        setObservacion((current) => ({ ...current, [name]: value }));
    };

    const limpiarFiltros = () => {
        setFiltros(filtrosIniciales);
        cargarDatos(filtrosIniciales);
    };

    const registrarObservacion = async (event) => {
        event.preventDefault();
        setLoading(true);
        setFeedback(null);
        try {
            await registrarEventoAuditoria(observacion);
            setObservacion(observacionInicial);
            setMostrarFormulario(false);
            setFeedback({ type: "success", message: "Observación de auditoría registrada correctamente." });
            await cargarDatos();
        } catch (err) {
            setFeedback({ type: "error", message: err.message || "No se pudo registrar la observación" });
        } finally {
            setLoading(false);
        }
    };

    const exportarCsv = async () => {
        try {
            const blob = await exportarEventosAuditoria(filtros);
            const url = URL.createObjectURL(blob);
            const link = document.createElement("a");
            link.href = url;
            link.download = "auditoria_farmape.csv";
            link.click();
            URL.revokeObjectURL(url);
        } catch (err) {
            setFeedback({ type: "error", message: err.message });
        }
    };

    return (
        <main className="audit-page">
            <header className="audit-header">
                <div>
                    <span className="audit-eyebrow">Control interno</span>
                    <h1><FiActivity /> Auditoría del sistema</h1>
                    <p>Revisa eventos automáticos y observaciones manuales sin modificar la bitácora histórica.</p>
                </div>
                <div className="audit-actions">
                    {puedeRegistrar && (
                        <button className="audit-btn secondary" onClick={() => setMostrarFormulario((value) => !value)}>
                            <FiPlusCircle /> Nueva observación
                        </button>
                    )}
                    <button className="audit-btn ghost" onClick={exportarCsv} disabled={loading}>
                        <FiDownload /> Exportar CSV
                    </button>
                    <button className="audit-btn" onClick={() => cargarDatos()} disabled={loading}>
                        <FiRefreshCcw /> Actualizar
                    </button>
                </div>
            </header>

            {feedback && <FeedbackMessage type={feedback.type} message={feedback.message} onClose={() => setFeedback(null)} />}

            <section className="audit-kpis">
                <article><span>Total eventos</span><strong>{resumen?.totalEventos ?? 0}</strong></article>
                <article><span>Eventos de hoy</span><strong>{resumen?.eventosHoy ?? 0}</strong></article>
                <article><span>Ventas auditadas</span><strong>{resumen?.eventosVentas ?? 0}</strong></article>
                <article><span>Compras auditadas</span><strong>{resumen?.eventosCompras ?? 0}</strong></article>
                <article><span>Inventario</span><strong>{resumen?.eventosInventario ?? 0}</strong></article>
                <article><span>Riesgo alto</span><strong>{resumen?.eventosRiesgo ?? 0}</strong></article>
            </section>

            <section className="audit-grid">
                <div className="audit-panel">
                    <h2>Eventos por módulo</h2>
                    <div className="audit-bars">
                        {(resumen?.modulos || []).map((item) => (
                            <div className="audit-bar-row" key={item.modulo}>
                                <span>{item.modulo}</span>
                                <div className="audit-bar-track"><div className="audit-bar-fill" style={{ width: `${Math.min(100, (item.total / Math.max(1, resumen.totalEventos)) * 100)}%` }} /></div>
                                <strong>{item.total}</strong>
                            </div>
                        ))}
                    </div>
                </div>
                <div className="audit-panel">
                    <h2>Acciones frecuentes</h2>
                    <div className="audit-chip-list">
                        {(resumen?.acciones || []).map((item) => <span key={item.accion} className="audit-chip">{item.accion} <b>{item.total}</b></span>)}
                    </div>
                </div>
            </section>

            {mostrarFormulario && puedeRegistrar && (
                <section className="audit-panel audit-form-panel">
                    <h2>Registrar observación manual</h2>
                    <p className="audit-note">Las observaciones manuales se guardan separadas de los eventos automáticos. La bitácora queda bloqueada contra edición y eliminación desde base de datos.</p>
                    <form className="audit-form" onSubmit={registrarObservacion}>
                        <label>Módulo<select name="modulo" value={observacion.modulo} onChange={handleObservacionChange}>{modulosDisponibles.map((modulo) => <option key={modulo} value={modulo}>{modulo}</option>)}</select></label>
                        <label>Entidad<input name="entidad" value={observacion.entidad} onChange={handleObservacionChange} required /></label>
                        <label>ID entidad<input name="entidadId" value={observacion.entidadId} onChange={handleObservacionChange} placeholder="Opcional" /></label>
                        <label>Acción<input name="accion" value={observacion.accion} onChange={handleObservacionChange} required /></label>
                        <label>Severidad<select name="severidad" value={observacion.severidad} onChange={handleObservacionChange}><option value="INFO">INFO</option><option value="BAJA">BAJA</option><option value="MEDIA">MEDIA</option><option value="ALTA">ALTA</option><option value="CRITICA">CRÍTICA</option></select></label>
                        <label className="audit-col-span">Descripción<textarea name="descripcion" value={observacion.descripcion} onChange={handleObservacionChange} required /></label>
                        <label>Valor anterior<textarea name="valorAnterior" value={observacion.valorAnterior} onChange={handleObservacionChange} /></label>
                        <label>Valor nuevo<textarea name="valorNuevo" value={observacion.valorNuevo} onChange={handleObservacionChange} /></label>
                        <div className="audit-form-actions audit-col-span">
                            <button className="audit-btn" type="submit" disabled={loading}>Guardar observación</button>
                            <button className="audit-btn ghost" type="button" onClick={() => setMostrarFormulario(false)}>Cancelar</button>
                        </div>
                    </form>
                </section>
            )}

            <section className="audit-panel">
                <div className="audit-panel-title">
                    <h2><FiFilter /> Filtros</h2>
                    <div><button className="audit-btn ghost" onClick={limpiarFiltros}>Limpiar</button><button className="audit-btn" onClick={() => cargarDatos()} disabled={loading}>Aplicar</button></div>
                </div>
                <div className="audit-filters">
                    <label>Módulo<select name="modulo" value={filtros.modulo} onChange={handleFiltroChange}><option value="">Todos</option>{modulosDisponibles.map((modulo) => <option key={modulo} value={modulo}>{modulo}</option>)}</select></label>
                    <label>Acción<input name="accion" value={filtros.accion} onChange={handleFiltroChange} placeholder="PAGO, ESTADO, COMPRA..." /></label>
                    <label>Severidad<select name="severidad" value={filtros.severidad} onChange={handleFiltroChange}><option value="">Todas</option><option value="INFO">INFO</option><option value="BAJA">BAJA</option><option value="MEDIA">MEDIA</option><option value="ALTA">ALTA</option><option value="CRITICA">CRÍTICA</option></select></label>
                    <label>Tipo<select name="tipoEvento" value={filtros.tipoEvento} onChange={handleFiltroChange}><option value="">Todos</option><option value="AUTOMATICO">Automático</option><option value="OBSERVACION">Observación</option><option value="MIGRACION">Migración</option></select></label>
                    <label>Usuario<input name="usuario" value={filtros.usuario} onChange={handleFiltroChange} placeholder="admin, cajero..." /></label>
                    <label>Desde<input type="date" name="desde" value={filtros.desde} onChange={handleFiltroChange} /></label>
                    <label>Hasta<input type="date" name="hasta" value={filtros.hasta} onChange={handleFiltroChange} /></label>
                    <label>Búsqueda<input name="q" value={filtros.q} onChange={handleFiltroChange} placeholder="Texto, entidad o valor" /></label>
                    <label>Límite<input type="number" min="10" max="500" name="limite" value={filtros.limite} onChange={handleFiltroChange} /></label>
                </div>
            </section>

            <section className="audit-panel audit-table-panel">
                <div className="audit-panel-title"><h2>Bitácora de eventos</h2><span>{loading ? "Cargando..." : `${eventos.length} evento(s)`}</span></div>
                {loading ? <LoadingState text="Cargando bitácora..." /> : eventos.length === 0 ? <EmptyState icon="🧾" title="Sin eventos" message="No hay eventos para los filtros seleccionados." /> : (
                    <div className="audit-table-wrap">
                        <table className="audit-table">
                            <thead><tr><th>Fecha</th><th>Módulo</th><th>Acción</th><th>Entidad</th><th>Usuario</th><th>Descripción</th><th>Severidad</th><th>Tipo</th><th>Origen</th></tr></thead>
                            <tbody>{eventos.map((evento) => (
                                <tr key={evento.idAuditoria}>
                                    <td>{formatoFecha(evento.fechaEvento)}</td>
                                    <td>{evento.modulo}</td>
                                    <td><span className="audit-action">{evento.accion}</span></td>
                                    <td>{evento.entidad}{evento.entidadId ? ` #${evento.entidadId}` : ""}</td>
                                    <td>{evento.usuario || "-"}</td>
                                    <td><div className="audit-desc">{evento.descripcion}</div>{(evento.valorAnterior || evento.valorNuevo) && <small>{evento.valorAnterior && <>Antes: {evento.valorAnterior} </>}{evento.valorNuevo && <>Después: {evento.valorNuevo}</>}</small>}</td>
                                    <td><span className={severidadClass(evento.severidad)}>{evento.severidad}</span></td>
                                    <td><span className="audit-type">{evento.tipoEvento || "AUTOMATICO"}</span></td>
                                    <td>{evento.origen}</td>
                                </tr>
                            ))}</tbody>
                        </table>
                    </div>
                )}
            </section>
        </main>
    );
};

export default Auditoria;
