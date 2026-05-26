import "./usuariosTable.css";

const UsuariosTable = ({
    usuarios,
    totalUsuarios,
    busqueda,
    setBusqueda,
    loading,
    paginaActual,
    paginaAnterior,
    paginaSiguiente,
    onEdit,
    onDelete,
}) => {

    const totalPaginas = 10;

    return (
        <div className="usuarios-table-section">

            <div className="usuarios-table-header">
                <div>
                    <h2>Usuarios registrados</h2>
                    <span>{totalUsuarios} usuarios encontrados</span>
                </div>

                <input
                    className="usuarios-search"
                    type="text"
                    placeholder="Buscar por DNI, nombre, email o rol..."
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                />
            </div>

            <div className="usuarios-table-wrapper">
                <table className="usuarios-table">
                    <thead>
                        <tr>
                            <th>DNI</th>
                            <th>Nombre completo</th>
                            <th>Email</th>
                            <th>Teléfono</th>
                            <th>Rol</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan="7" className="usuarios-empty">
                                    Cargando usuarios...
                                </td>
                            </tr>
                        ) : usuarios.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="usuarios-empty">
                                    No hay usuarios para mostrar
                                </td>
                            </tr>
                        ) : (
                            usuarios.map((usuario) => (
                                <tr key={usuario.id}>
                                    <td>{usuario.dni}</td>
                                    <td>{usuario.nombres} {usuario.apellidos}</td>
                                    <td>{usuario.usuario || usuario.email}</td>
                                    <td>{usuario.telefono}</td>
                                    <td>{usuario.rol}</td>
                                    <td>
                                        <span className="usuarios-status">
                                            {usuario.estado || "Activo"}
                                        </span>
                                    </td>
                                    <td>
                                        <div className="usuarios-actions">
                                            <button onClick={() => onEdit(usuario)}>
                                                Editar
                                            </button>

                                            <button
                                                className="delete"
                                                onClick={() => onDelete(usuario.id)}
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

            <div className="usuarios-pagination">
                <button
                    onClick={paginaAnterior}
                    disabled={paginaActual === 1}
                >
                    ←
                </button>

                <span>
                    Página {paginaActual} de {totalPaginas || 1}
                </span>

                <button
                    onClick={paginaSiguiente}
                    disabled={paginaActual === totalPaginas || totalPaginas === 0}
                >
                    →
                </button>
            </div>
        </div>
    );
};

export default UsuariosTable;