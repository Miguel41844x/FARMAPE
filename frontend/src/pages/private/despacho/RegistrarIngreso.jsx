import "./DespachoPages.css";

function RegistrarIngreso() {
    return (
        <div className="despacho-module">
        <div className="despacho-header">
            <h1>Ingreso de productos</h1>
            <p>Registra productos recibidos y actualiza el stock del almacén.</p>
        </div>

        <section className="despacho-table-section">
            <div className="despacho-table-header">
            <div>
                <h2>Productos recibidos</h2>
                <span>Registro de ingresos al almacén</span>
            </div>

            <div className="despacho-filters">
                <input className="despacho-search" placeholder="Buscar producto o lote" />
                <select className="despacho-select">
                <option>Todos los proveedores</option>
                <option>Farma Perú</option>
                <option>Distribuidora Andina</option>
                </select>
            </div>
            </div>

            <div className="despacho-table-wrapper">
            <table className="despacho-table">
                <thead>
                <tr>
                    <th>Producto</th>
                    <th>Cantidad</th>
                    <th>Lote</th>
                    <th>Vencimiento</th>
                    <th>Proveedor</th>
                    <th>Acciones</th>
                </tr>
                </thead>

                <tbody>
                <tr>
                    <td>Paracetamol 500mg</td>
                    <td>100</td>
                    <td>LT-2026</td>
                    <td>20/12/2026</td>
                    <td>Farma Perú</td>
                    <td>
                    <div className="despacho-actions">
                        <button>Registrar</button>
                    </div>
                    </td>
                </tr>

                <tr>
                    <td>Ibuprofeno 400mg</td>
                    <td>80</td>
                    <td>IB-4589</td>
                    <td>15/09/2026</td>
                    <td>Distribuidora Andina</td>
                    <td>
                    <div className="despacho-actions">
                        <button>Registrar</button>
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

export default RegistrarIngreso;