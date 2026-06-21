import "./maintenanceTable.css";

const modules = [
    {
        modulo: "Usuarios",
        registros: 12,
        estado: "Activo",
        ultimaActualizacion: "Hoy",
    },
    {
        modulo: "Productos",
        registros: 145,
        estado: "Activo",
        ultimaActualizacion: "Hoy",
    },
];

const MaintenanceTable = () => {
    return (
        <div className="maintenance-table-section">
        <div className="maintenance-table-header">
            <h2>Resumen de mantenimiento</h2>
        </div>

        <div className="maintenance-table-wrapper">
            <table className="maintenance-table">
            <thead>
                <tr>
                <th>Módulo</th>
                <th>Registros</th>
                <th>Estado</th>
                <th>Última actualización</th>
                <th>Acción</th>
                </tr>
            </thead>

            <tbody>
                {modules.map((item) => (
                    <tr key={item.modulo}>
                        <td className="module-name">{item.modulo}</td>
                        <td>{item.registros}</td>
                        <td>
                            <span className="status-badge">{item.estado}</span>
                        </td>
                        <td>{item.ultimaActualizacion}</td>
                        <td>
                        <button className="maintenance-action-btn">
                            Gestionar
                        </button>
                        </td>
                    </tr>
                ))}
            </tbody>
            </table>
        </div>
        </div>
    );
};

export default MaintenanceTable;
