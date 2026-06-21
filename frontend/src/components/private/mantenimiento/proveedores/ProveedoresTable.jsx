import "./proveedoresTable.css";

const ProveedoresTable = ({
    proveedores,
    totalProveedores,
    busqueda,
    setBusqueda,
    loading,
    paginaActual,
    totalPaginas,
    paginaAnterior,
    paginaSiguiente,
    onEdit,
    onDelete,
}) => {
    return (
        <section className="proveedores-table-section">
            <div className="proveedores-table-header">
                <div>
                    <h2>Listado de proveedores</h2>
                    <span>{totalProveedores} proveedores registrados</span>
                </div>

                <input
                    className="proveedores-search"
                    type="text"
                    placeholder="Buscar por RUC, razón social, email o tipo"
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                />
            </div>

            <div className="proveedores-table-wrapper">
                <table className="proveedores-table">
                    <thead>
                        <tr>
                            <th>RUC</th>
                            <th>Razón social</th>
                            <th>Teléfono</th>
                            <th>Email</th>
                            <th>Dirección</th>
                            <th>Tipo</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan="7" className="proveedores-empty">
                                    Cargando proveedores...
                                </td>
                            </tr>
                        ) : proveedores.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="proveedores-empty">
                                    No se encontraron proveedores
                                </td>
                            </tr>
                        ) : (
                            proveedores.map((proveedor) => (
                                <tr key={proveedor.idProveedor}>
                                    <td>{proveedor.ruc}</td>
                                    <td>
                                        <strong>{proveedor.razonSocial}</strong>
                                    </td>
                                    <td>{proveedor.telefono}</td>
                                    <td>{proveedor.email}</td>
                                    <td>{proveedor.direccion}</td>
                                    <td>
                                        <span className="proveedores-status">
                                            {proveedor.tipoProveedor}
                                        </span>
                                    </td>
                                    <td>
                                        <div className="proveedores-actions">
                                            <button onClick={() => onEdit(proveedor)}>
                                                Editar
                                            </button>

                                            <button
                                                className="delete"
                                                onClick={() =>
                                                    onDelete(proveedor.idProveedor)
                                                }
                                            >
                                                Eliminar
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                    </tbody>
                </table>
            </div>

            <div className="proveedores-pagination">
                <button onClick={paginaAnterior} disabled={paginaActual === 1}>
                    ‹
                </button>

                <span>
                    Página {paginaActual} de {totalPaginas || 1}
                </span>

                <button
                    onClick={paginaSiguiente}
                    disabled={paginaActual === totalPaginas || totalPaginas === 0}
                >
                    ›
                </button>
            </div>
        </section>
    );
};

export default ProveedoresTable;