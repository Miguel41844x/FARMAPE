import "./DespachoPages.css";

function VerificarProductos() {
    return (
        <div className="despacho-module">
        <div className="despacho-header">
            <h1>Verificación de productos</h1>
            <p>Compara los productos pedidos con los productos recibidos.</p>
        </div>

        <section className="despacho-table-section">
            <div className="despacho-table-header">
            <div>
                <h2>Comparación de productos</h2>
                <span>Control de conformidad de pedidos de compra</span>
            </div>

            <div className="despacho-filters">
                <input className="despacho-search" placeholder="Buscar pedido o producto" />
                <select className="despacho-select">
                <option>Todos los estados</option>
                <option>Conforme</option>
                <option>Observado</option>
                </select>
            </div>
            </div>

            <div className="despacho-table-wrapper">
            <table className="despacho-table">
                <thead>
                <tr>
                    <th>Producto</th>
                    <th>Pedido</th>
                    <th>Recibido</th>
                    <th>Diferencia</th>
                    <th>Estado</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>Paracetamol 500mg</td>
                    <td>100</td>
                    <td>100</td>
                    <td>0</td>
                    <td><span className="estado conforme">Conforme</span></td>
                    <td>
                    <div className="despacho-actions">
                        <button>Confirmar</button>
                    </div>
                    </td>
                </tr>

                <tr>
                    <td>Ibuprofeno 400mg</td>
                    <td>50</td>
                    <td>40</td>
                    <td>-10</td>
                    <td><span className="estado observado">Observado</span></td>
                    <td>
                    <div className="despacho-actions">
                        <button>Observar</button>
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

export default VerificarProductos;