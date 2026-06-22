import { useEffect, useMemo, useState } from "react";
import { FiActivity, FiFilter, FiRefreshCcw, FiPlusCircle } from "react-icons/fi";
import { useAuth } from "../../../context/AuthContext";
import { PERMISSIONS } from "../../../constants/permissions";
import {
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
    desde: "",
    hasta: "",
    q: "",
    limite: 100,
};

const eventoInicial = {
    modulo: "General",
    entidad: "Evento manual",
    entidadId: "",
    accion: "REVISION",
    descripcion: "",
    valorAnterior: "",
    valorNuevo: "",
    severidad: "INFO",
};

const formatoFecha = (fecha) => {
    if (!fecha) return "-";
    return new Intl.DateTimeFormat("es-PE", {
        dateStyle: "short",
        timeStyle: "short",
    }).format(new Date(fecha));
};

const severidadClass = (severidad) => `audit-badge audit-${(severidad || "info").toLowerCase()}`;

const Auditoria = () => {
    const { hasPermission } = useAuth();
    const puedeRegistrar = hasPermission(PERMISSIONS.AUDIT_MANAGE);

    const [resumen, setResumen] = useState(null);
    const [eventos, setEventos] = useState([]);
    const [filtros, setFiltros] = useState(filtrosIniciales);
    const [nuevoEvento, setNuevoEvento] = useState(eventoInicial);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [mensaje, setMensaje] = useState("");
    const [mostrarFormulario, setMostrarFormulario] = useState(false);

    const modulosDisponibles = useMemo(() => {
        const base = ["Ventas", "Compras", "Almacén", "Inventario", "Caja", "Usuarios", "Roles", "Recetas", "Reportes", "Seguridad", "General"];
        const dinamicos = eventos.map((evento) => evento.modulo).filter(Boolean);
        return [...new Set([...base, ...dinamicos])];
    }, [eventos]);

    const cargarDatos = async () => {
        setLoading(true);
        setError("");
        setMensaje("");

        try {
            const [resumenData, eventosData] = await Promise.all([
                obtenerResumenAuditoria(),
                listarEventosAuditoria(filtros),
            ]);
            setResumen(resumenData);
            setEventos(eventosData);
        } catch (err) {
            setError(err.message || "No se pudo cargar auditoría");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarDatos();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleFiltroChange = (event) => {
        const { name, value } = event.target;
        setFiltros((current) => ({ ...current, [name]: value }));
    };

    const handleNuevoEventoChange = (event) => {
        const { name, value } = event.target;
        setNuevoEvento((current) => ({ ...current, [name]: value }));
    };

    const limpiarFiltros = () => {
        setFiltros(filtrosIniciales);
    };

    const registrarEvento = async (event) => {
        event.preventDefault();
        setLoading(true);
        setError("");
        setMensaje("");

        try {
            await registrarEventoAuditoria(nuevoEvento);
            setNuevoEvento(eventoInicial);
            setMostrarFormulario(false);
            setMensaje("Evento de auditoría registrado correctamente.");
            await cargarDatos();
        } catch (err) {
            setError(err.message || "No se pudo registrar el evento");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="audit-page">
            <header className="audit-header">
                <div>
                    <span className="audit-eyebrow">Control interno</span>
                    <h1><FiActivity /> Auditoría del sistema</h1>
                    <p>
                        Revisa cambios de ventas, compras, almacén, usuarios, roles y eventos sensibles del sistema FARMAPE.
                    </p>
                </div>

                <div className="audit-actions">
                    {puedeRegistrar && (
                        <button className="audit-btn secondary" onClick={() => setMostrarFormulario((value) => !value)}>
                            <FiPlusCircle /> Registrar evento
                        </button>
                    )}
                    <button className="audit-btn" onClick={cargarDatos} disabled={loading}>
                        <FiRefreshCcw /> Actualizar
                    </button>
                </div>
            </header>

            {error && <div className="audit-alert error">{error}</div>}
            {mensaje && <div className="audit-alert success">{mensaje}</div>}

            <section className="audit-kpis">
                <article>
                    <span>Total eventos</span>
                    <strong>{resumen?.totalEventos ?? 0}</strong>
                </article>
                <article>
                    <span>Eventos de hoy</span>
                    <strong>{resumen?.eventosHoy ?? 0}</strong>
                </article>
                <article>
                    <span>Ventas auditadas</span>
                    <strong>{resumen?.eventosVentas ?? 0}</strong>
                </article>
                <article>
                    <span>Compras auditadas</span>
                    <strong>{resumen?.eventosCompras ?? 0}</strong>
                </article>
                <article>
                    <span>Inventario</span>
                    <strong>{resumen?.eventosInventario ?? 0}</strong>
                </article>
                <article>
                    <span>Riesgo alto</span>
                    <strong>{resumen?.eventosRiesgo ?? 0}</strong>
                </article>
            </section>

            <section className="audit-grid">
                <div className="audit-panel">
                    <h2>Eventos por módulo</h2>
                    <div className="audit-bars">
                        {(resumen?.modulos || []).map((item) => (
                            <div className="audit-bar-row" key={item.modulo}>
                                <span>{item.modulo}</span>
                                <div className="audit-bar-track">
                                    <div
                                        className="audit-bar-fill"
                                        style={{ width: `${Math.min(100, (item.total / Math.max(1, resumen.totalEventos)) * 100)}%` }}
                                    />
                                </div>
                                <strong>{item.total}</strong>
                            </div>
                        ))}
                    </div>
                </div>

                <div className="audit-panel">
                    <h2>Acciones frecuentes</h2>
                    <div className="audit-chip-list">
                        {(resumen?.acciones || []).map((item) => (
                            <span key={item.accion} className="audit-chip">
                                {item.accion} <b>{item.total}</b>
                            </span>
                        ))}
                    </div>
                </div>
            </section>

            {mostrarFormulario && puedeRegistrar && (
                <section className="audit-panel audit-form-panel">
                    <h2>Registrar evento manual</h2>
                    <form className="audit-form" onSubmit={registrarEvento}>
                        <label>
                            Módulo
                            <select name="modulo" value={nuevoEvento.modulo} onChange={handleNuevoEventoChange}>
                                {modulosDisponibles.map((modulo) => (
                                    <option key={modulo} value={modulo}>{modulo}</option>
                                ))}
                            </select>
                        </label>
                        <label>
                            Entidad
                            <input name="entidad" value={nuevoEvento.entidad} onChange={handleNuevoEventoChange} required />
                        </label>
                        <label>
                            ID entidad
                            <input name="entidadId" value={nuevoEvento.entidadId} onChange={handleNuevoEventoChange} placeholder="Opcional" />
                        </label>
                        <label>
                            Acción
                            <input name="accion" value={nuevoEvento.accion} onChange={handleNuevoEventoChange} required />
                        </label>
                        <label>
                            Severidad
                            <select name="severidad" value={nuevoEvento.severidad} onChange={handleNuevoEventoChange}>
                                <option value="INFO">INFO</option>
                                <option value="BAJA">BAJA</option>
                                <option value="MEDIA">MEDIA</option>
                                <option value="ALTA">ALTA</option>
                                <option value="CRITICA">CRÍTICA</option>
                            </select>
                        </label>
                        <label className="audit-col-span">
                            Descripción
                            <textarea name="descripcion" value={nuevoEvento.descripcion} onChange={handleNuevoEventoChange} required />
                        </label>
                        <label>
                            Valor anterior
                            <textarea name="valorAnterior" value={nuevoEvento.valorAnterior} onChange={handleNuevoEventoChange} />
                        </label>
                        <label>
                            Valor nuevo
                            <textarea name="valorNuevo" value={nuevoEvento.valorNuevo} onChange={handleNuevoEventoChange} />
                        </label>
                        <div className="audit-form-actions audit-col-span">
                            <button className="audit-btn" type="submit" disabled={loading}>Guardar evento</button>
                            <button className="audit-btn ghost" type="button" onClick={() => setMostrarFormulario(false)}>Cancelar</button>
                        </div>
                    </form>
                </section>
            )}

            <section className="audit-panel">
                <div className="audit-panel-title">
                    <h2><FiFilter /> Filtros</h2>
                    <div>
                        <button className="audit-btn ghost" onClick={limpiarFiltros}>Limpiar</button>
                        <button className="audit-btn" onClick={cargarDatos} disabled={loading}>Aplicar</button>
                    </div>
                </div>

                <div className="audit-filters">
                    <label>
                        Módulo
                        <select name="modulo" value={filtros.modulo} onChange={handleFiltroChange}>
                            <option value="">Todos</option>
                            {modulosDisponibles.map((modulo) => (
                                <option key={modulo} value={modulo}>{modulo}</option>
                            ))}
                        </select>
                    </label>
                    <label>
                        Acción
                        <input name="accion" value={filtros.accion} onChange={handleFiltroChange} placeholder="PAGO, ESTADO, COMPRA..." />
                    </label>
                    <label>
                        Severidad
                        <select name="severidad" value={filtros.severidad} onChange={handleFiltroChange}>
                            <option value="">Todas</option>
                            <option value="INFO">INFO</option>
                            <option value="BAJA">BAJA</option>
                            <option value="MEDIA">MEDIA</option>
                            <option value="ALTA">ALTA</option>
                            <option value="CRITICA">CRÍTICA</option>
                        </select>
                    </label>
                    <label>
                        Usuario
                        <input name="usuario" value={filtros.usuario} onChange={handleFiltroChange} placeholder="admin, cajero..." />
                    </label>
                    <label>
                        Desde
                        <input type="date" name="desde" value={filtros.desde} onChange={handleFiltroChange} />
                    </label>
                    <label>
                        Hasta
                        <input type="date" name="hasta" value={filtros.hasta} onChange={handleFiltroChange} />
                    </label>
                    <label>
                        Búsqueda
                        <input name="q" value={filtros.q} onChange={handleFiltroChange} placeholder="Texto, entidad o valor" />
                    </label>
                    <label>
                        Límite
                        <input type="number" min="10" max="500" name="limite" value={filtros.limite} onChange={handleFiltroChange} />
                    </label>
                </div>
            </section>

            <section className="audit-panel audit-table-panel">
                <div className="audit-panel-title">
                    <h2>Bitácora de eventos</h2>
                    <span>{loading ? "Cargando..." : `${eventos.length} evento(s)`}</span>
                </div>

                <div className="audit-table-wrap">
                    <table className="audit-table">
                        <thead>
                            <tr>
                                <th>Fecha</th>
                                <th>Módulo</th>
                                <th>Acción</th>
                                <th>Entidad</th>
                                <th>Usuario</th>
                                <th>Descripción</th>
                                <th>Severidad</th>
                                <th>Origen</th>
                            </tr>
                        </thead>
                        <tbody>
                            {eventos.map((evento) => (
                                <tr key={evento.idAuditoria}>
                                    <td>{formatoFecha(evento.fechaEvento)}</td>
                                    <td>{evento.modulo}</td>
                                    <td><span className="audit-action">{evento.accion}</span></td>
                                    <td>{evento.entidad}{evento.entidadId ? ` #${evento.entidadId}` : ""}</td>
                                    <td>{evento.usuario || "-"}</td>
                                    <td>
                                        <div className="audit-desc">{evento.descripcion}</div>
                                        {(evento.valorAnterior || evento.valorNuevo) && (
                                            <small>
                                                {evento.valorAnterior && <>Antes: {evento.valorAnterior} </>}
                                                {evento.valorNuevo && <>Después: {evento.valorNuevo}</>}
                                            </small>
                                        )}
                                    </td>
                                    <td><span className={severidadClass(evento.severidad)}>{evento.severidad}</span></td>
                                    <td>{evento.origen}</td>
                                </tr>
                            ))}

                            {!loading && eventos.length === 0 && (
                                <tr>
                                    <td colSpan="8" className="audit-empty">No hay eventos para los filtros seleccionados.</td>
                                </tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    );
};

export default Auditoria;
