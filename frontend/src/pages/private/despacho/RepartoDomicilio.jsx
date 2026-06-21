import "./DespachoPages.css";

function RepartoDomicilio() {
    return (
        <div className="despacho-module">
        <div className="despacho-header">
            <h1>Reparto a domicilio</h1>
            <p>Controla los pedidos enviados por delivery.</p>
        </div>

        <section className="despacho-table-section">
            <div className="despacho-table-header">
            <div>
                <h2>Órdenes para reparto</h2>
                <span>Pedidos pendientes, en ruta y entregados</span>
            </div>

            <div className="despacho-filters">
                <input className="despacho-search" placeholder="Buscar por orden o cliente" />
                <select className="despacho-select">
                <option>Todos los estados</option>
                <option>Pendiente</option>
                <option>En ruta</option>
                <option>Entregado</option>
                </select>
            </div>
            </div>

            <div className="despacho-table-wrapper">
            <table className="despacho-table">
                <thead>
                <tr>
                    <th>Orden</th>
                    <th>Cliente</th>
                    <th>Dirección</th>
                    <th>Repartidor</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>OV-0002</td>
                    <td>María López</td>
                    <td>Av. Perú 123</td>
                    <td>Carlos Ramos</td>
                    <td><span className="estado ruta">En ruta</span></td>
                    <td>
                    <div className="despacho-actions">
                        <button>Entregado</button>
                    </div>
                    </td>
                </tr>

                <tr>
                    <td>OV-0003</td>
                    <td>Luis Torres</td>
                    <td>Jr. Lima 456</td>
                    <td>Sin asignar</td>
                    <td><span className="estado pendiente">Pendiente</span></td>
                    <td>
                    <div className="despacho-actions">
                        <button>Asignar</button>
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

export default RepartoDomicilio;