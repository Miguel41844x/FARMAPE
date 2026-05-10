import "./ordersTable.css";

/*
 * Lista temporal de órdenes.
 * Luego puede reemplazarse por data proveniente de una API.
 */

const orders = [
    {
        id: "#004",
        cliente: "Sebastián Barrantes",
        canal: "Virtual",
        total: "S/ 35",
        estado: "Ordenado",
    },
    {
        id: "#003",
        cliente: "Jimena Flores",
        canal: "Presencial",
        total: "S/ 65",
        estado: "Pagado",
    },
    {
        id: "#002",
        cliente: "Angelo Paredes",
        canal: "Presencial",
        total: "S/ 26",
        estado: "Pagado",
    },
    {
        id: "#001",
        cliente: "Kiara Chavez",
        canal: "Presencial",
        total: "S/ 12",
        estado: "Entregado",
    },
];

/**
 * Componente OrdersTable
 *
 * Muestra una tabla con las últimas órdenes registradas
 * dentro del sistema.
 *
 * Estructura:
 * - Header con título y botón principal
 * - Render dinámico usando map()
 */

export default function OrdersTable() {
  return (
        <div className="orders-container">

        <div className="orders-header">

            <h2>Últimas órdenes</h2>

            <button className="view-btn">
            Ver todas las órdenes
            </button>
        </div>

        {/* Contenedor responsive de tabla */}
        <div className="table-wrapper">

            <table className="orders-table">

            {/* Encabezado de columnas */}
            <thead>
                <tr>
                <th>ID</th>
                <th>Cliente</th>
                <th>Canal</th>
                <th>Total</th>
                <th>Estado</th>
                <th>Acción</th>
                </tr>
            </thead>

            {/* Cuerpo de la tabla */}
            <tbody>

                {/* Recorremos las órdenes */}
                {orders.map((order) => (
                <tr key={order.id}>


                    <td className="id">
                    {order.id}
                    </td>

                    <td className="cliente">
                    {order.cliente}
                    </td>

                    <td>
                    <span className="badge canal">
                        {order.canal}
                    </span>
                    </td>

                    <td className="total">
                    {order.total}
                    </td>

                    <td>
                    <span className="badge estado">
                        {order.estado}
                    </span>
                    </td>

                    <td>
                    <button className="details-btn">
                        Detalles
                    </button>
                    </td>

                </tr>
                ))}

            </tbody>
            </table>
        </div>
        </div>
    );
}