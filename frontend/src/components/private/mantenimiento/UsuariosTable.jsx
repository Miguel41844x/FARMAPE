import "./usuariosTable.css";

const UsuariosTable = ({ usuarios, onEdit, onDelete }) => {
    return (
        <div className="usuarios-table-section">
            <div className="usuarios-table-header">
                <h2>Usuarios registrados</h2>
                <span>{usuarios.length} usuarios</span>
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
                        {usuarios.length === 0 ? (
                            <tr>
                                <td colSpan="7" className="usuarios-empty">
                                    No hay usuarios registrados
                                </td>
                            </tr>
                        ) : (
                            usuarios.map((usuario) => (
                                <tr key={usuario.id}>
                                    <td>{usuario.dni}</td>
                                    <td>{usuario.nombres} {usuario.apellidos}</td>
                                    <td>{usuario.email}</td>
                                    <td>{usuario.telefono}</td>
                                    <td>{usuario.rol}</td>
                                    <td>
                                        <span className="usuarios-status">
                                            {usuario.estado}
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
        </div>
    );
};

export default UsuariosTable;
