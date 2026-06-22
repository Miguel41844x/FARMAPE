import EmptyState from "../../common/EmptyState";
import LoadingState from "../../common/LoadingState";
import "./recetasListaTable.css";

const ESTADOS_SIGUIENTES = {
    Registrada: "Validada",
    Validada: "Presupuestada",
    Presupuestada: "Aprobada",
    Aprobada: "En Elaboracion",
    "En Elaboracion": "Preparada",
    Preparada: "Entregada",
    Lista: "Entregada",
};

const estadoClase = (estado) => String(estado || "sin-estado").toLowerCase().replaceAll("_", "-").replaceAll(" ", "-");

const RecetasListaTable = ({
    recetas,
    loading,
    busqueda,
    setBusqueda,
    paginaActual,
    setPaginaActual,
    onRegistrarClick,
    onCambiarEstado,
}) => {
    const recetasPorPagina = 10;
    const totalPaginas = recetas.length > 0 ? Math.ceil(recetas.length / recetasPorPagina) : 1;
    const paginaSegura = Math.min(paginaActual, totalPaginas);
    const inicio = (paginaSegura - 1) * recetasPorPagina;
    const recetasPagina = recetas.slice(inicio, inicio + recetasPorPagina);

    return (
        <div className="recetas-table-section">
            <div className="recetas-table-header">
                <div>
                    <h2>Recetas en seguimiento</h2>
                    <span>{recetas.length} recetas encontradas en el sistema</span>
                </div>
                <div className="recetas-header-actions">
                    <button type="button" className="btn-registrar-receta" onClick={onRegistrarClick}>
                        + Registrar receta médica
                    </button>
                    <input
                        className="recetas-search"
                        type="text"
                        placeholder="Buscar por paciente, médico, descripción o estado..."
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />
                </div>
            </div>

            {loading ? (
                <LoadingState text="Cargando recetas magistrales..." />
            ) : recetas.length === 0 ? (
                <EmptyState
                    icon="📋"
                    title="No hay recetas registradas"
                    message="Registra una receta prescrita por un médico para iniciar la validación farmacéutica y el presupuesto."
                    actionText="Registrar receta"
                    onAction={onRegistrarClick}
                />
            ) : (
                <div className="recetas-table-wrapper">
                    <table className="recetas-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Paciente</th>
                                <th>Médico</th>
                                <th>Descripción</th>
                                <th>Presupuesto</th>
                                <th>Estado receta</th>
                                <th>Estado fórmula</th>
                                <th>Orden</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {recetasPagina.map((receta) => {
                                const siguiente = ESTADOS_SIGUIENTES[receta.estado];
                                const puedeAvanzar = Boolean(siguiente) && receta.estado !== "Entregada" && receta.estado !== "Anulada";
                                return (
                                    <tr key={receta.idReceta}>
                                        <td>#{receta.idReceta}</td>
                                        <td>
                                            <strong>{receta.nombrePaciente || "Paciente"}</strong>
                                            <small>{receta.dniPaciente}</small>
                                        </td>
                                        <td>{receta.medicoPrescriptor || "-"}</td>
                                        <td><div className="receta-desc-cell">{receta.descripcionReceta}</div></td>
                                        <td>S/. {Number(receta.presupuesto || 0).toFixed(2)}</td>
                                        <td><span className={`recetas-status status-${estadoClase(receta.estado)}`}>{receta.estado || "Sin estado"}</span></td>
                                        <td>{receta.estadoFormula ? <span className={`recetas-status status-${estadoClase(receta.estadoFormula)}`}>{receta.estadoFormula}</span> : "-"}</td>
                                        <td>{receta.idOrdenVenta ? `OV-${String(receta.idOrdenVenta).padStart(6, "0")}` : "-"}</td>
                                        <td>
                                            <div className="receta-actions">
                                                {puedeAvanzar && (
                                                    <button type="button" onClick={() => onCambiarEstado(receta, siguiente)}>
                                                        Pasar a {siguiente}
                                                    </button>
                                                )}
                                                {receta.estado !== "Anulada" && receta.estado !== "Entregada" && (
                                                    <button type="button" className="danger" onClick={() => onCambiarEstado(receta, "Anulada")}>
                                                        Anular
                                                    </button>
                                                )}
                                            </div>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            )}

            {recetas.length > 0 && (
                <div className="recetas-pagination">
                    <button onClick={() => setPaginaActual((p) => Math.max(p - 1, 1))} disabled={paginaSegura === 1}>←</button>
                    <span>Página {paginaSegura} de {totalPaginas}</span>
                    <button onClick={() => setPaginaActual((p) => Math.min(p + 1, totalPaginas))} disabled={paginaSegura === totalPaginas}>→</button>
                </div>
            )}
        </div>
    );
};

export default RecetasListaTable;
