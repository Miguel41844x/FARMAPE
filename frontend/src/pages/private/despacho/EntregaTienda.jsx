import "./DespachoPages.css";

function EntregaTienda() {
    return (
        <div className="despacho-module">
        <div className="despacho-header">
            <h1>Entrega en tienda</h1>
            <p>Gestiona la entrega de órdenes pagadas al cliente.</p>
        </div>

        <section className="despacho-table-section">
            <div className="despacho-table-header">
            <div>
                <h2>Órdenes pendientes de despacho</h2>
                <span>Listado de órdenes listas para entregar</span>
            </div>

            <div className="despacho-filters">
                <input
                className="despacho-search"
                placeholder="Buscar por orden o cliente"
                />

                <select className="despacho-select">
                <option value="">Todos los estados</option>
                <option value="pagado">Pagado</option>
                <option value="entregado">Entregado</option>
                <option value="pendiente">Pendiente</option>
                </select>
            </div>
            </div>

            <div className="despacho-table-wrapper">
            <table className="despacho-table">
                <thead>
                <tr>
                    <th>Orden</th>
                    <th>Cliente</th>
                    <th>Comprobante</th>
                    <th>Total</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>OV-0001</td>
                    <td>Juan Pérez</td>
                    <td>Boleta B001-25</td>
                    <td>S/ 85.00</td>
                    <td>
                    <span className="estado pagado">Pagado</span>
                    </td>
                    <td>
                    <div className="despacho-actions">
                        <button>Entregar</button>
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

export default EntregaTienda;