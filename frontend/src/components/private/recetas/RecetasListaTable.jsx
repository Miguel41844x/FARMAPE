import React from "react";
import "./recetasListaTable.css";

const RecetasListaTable = ({ 
    recetas, 
    loading, 
    busqueda, 
    setBusqueda, 
    paginaActual, 
    setPaginaActual,
    onRegistrarClick
}) => {
    const totalPaginas = recetas.length > 0 ? Math.ceil(recetas.length / 10) : 1;

    return (
        <div className="recetas-table-section">
            <div className="recetas-table-header">
                <div>
                    <h2>Recetas en Seguimiento</h2>
                    <span>{recetas.length} recetas encontradas en el sistema</span>
                </div>
                <div className="recetas-header-actions">
                    <button 
                        type="button" 
                        className="btn-registrar-receta"
                        onClick={onRegistrarClick}
                    >
                        + Registrar Receta Médica
                    </button>
                    <input
                        className="recetas-search"
                        type="text"
                        placeholder="Buscar por médico, descripción o estado..."
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />
                </div>
            </div>

            <div className="recetas-table-wrapper">
                <table className="recetas-table">
                    <thead>
                        <tr>
                            <th>ID Receta</th>
                            <th>DNI Paciente</th>
                            <th>Médico Prescriptor</th>
                            <th>Descripción Receta</th>
                            <th>Presupuesto (S/.)</th>
                            <th>Estado</th>
                        </tr>
                    </thead>
                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan="6" className="recetas-empty">
                                    Cargando flujo de recetas médicas...
                                </td>
                            </tr>
                        ) : recetas.length === 0 ? (
                            <tr>
                                <td colSpan="6" className="recetas-empty-container">
                                    <div className="empty-state-card">
                                        <div className="empty-icon">📋</div>
                                        <h3>No hay recetas registradas...</h3>
                                        <p>Utilice el botón superior para registrar la primera receta médica prescrita por el médico tratante e iniciar la validación de seguridad.</p>
                                    </div>
                                </td>
                            </tr>
                        ) : (
                            recetas.map((receta) => (
                                <tr key={receta.idReceta}>
                                    <td>#{receta.idReceta}</td>
                                    <td>{receta.dniPaciente}</td>
                                    <td>{receta.medicoPrescriptor}</td>
                                    <td>{receta.descripcionReceta}</td>
                                    <td>S/. {Number(receta.presupuesto || 0).toFixed(2)}</td>
                                    <td>
                                        <span className={`recetas-status status-${String(receta.estado || "").toLowerCase().replaceAll("_", "-").replaceAll(" ", "-")}`}>
                                            {receta.estado}
                                        </span>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            {recetas.length > 0 && (
                <div className="recetas-pagination">
                    <button 
                        onClick={() => setPaginaActual(p => Math.max(p - 1, 1))}
                        disabled={paginaActual === 1}
                    >
                        ←
                    </button>
                    <span>Página {paginaActual} de {totalPaginas}</span>
                    <button 
                        onClick={() => setPaginaActual(p => Math.min(p + 1, totalPaginas))}
                        disabled={paginaActual === totalPaginas}
                    >
                        →
                    </button>
                </div>
            )}
        </div>
    );
};

export default RecetasListaTable;