import "./DespachoPages.css";

function InformeAlmacen() {
    return (
        <div className="despacho-module">
        <div className="despacho-header">
            <h1>Informe de almacén</h1>
            <p>Consulta movimientos, ingresos, stock bajo y productos próximos a vencer.</p>
        </div>

        <section className="despacho-table-section">
            <div className="despacho-table-header">
            <div>
                <h2>Resumen de almacén</h2>
                <span>Indicadores principales del inventario</span>
            </div>

            <div className="despacho-filters">
                <input className="despacho-search" type="date" />
                <select className="despacho-select">
                <option>Hoy</option>
                <option>Esta semana</option>
                <option>Este mes</option>
                </select>
            </div>
            </div>

            <div className="despacho-table-wrapper">
            <table className="despacho-table">
                <thead>
                <tr>
                    <th>Indicador</th>
                    <th>Cantidad</th>
                    <th>Detalle</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>Productos ingresados</td>
                    <td>25</td>
                    <td>Ingresos registrados hoy</td>
                    <td>
                    <div className="despacho-actions">
                        <button>Ver detalle</button>
                    </div>
                    </td>
                </tr>

                <tr>
                    <td>Stock bajo</td>
                    <td>8</td>
                    <td>Productos por reponer</td>
                    <td>
                    <div className="despacho-actions">
                        <button>Ver productos</button>
                    </div>
                    </td>
                </tr>

                <tr>
                    <td>Próximos a vencer</td>
                    <td>5</td>
                    <td>Vencimiento menor a 30 días</td>
                    <td>
                    <div className="despacho-actions">
                        <button>Revisar</button>
                    </div>
                    </td>
                </tr>
                </tbody>
            </table>
            </div>
        </section>
        </div>
    );
}

export default InformeAlmacen;