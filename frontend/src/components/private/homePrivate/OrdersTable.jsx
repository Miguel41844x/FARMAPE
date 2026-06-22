import { useEffect, useState } from "react";
import { obtenerUltimasOrdenes } from "../../../services/ventas/ventaService";
import "./ordersTable.css";

export default function OrdersTable() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let activo = true;
        obtenerUltimasOrdenes()
            .then((data) => activo && setOrders(data))
            .catch((error) => console.error("Error al cargar últimas órdenes:", error))
            .finally(() => activo && setLoading(false));
        return () => { activo = false; };
    }, []);

    return (
        <section className="orders-container">
            <div className="orders-header">
                <div><h2>Últimas órdenes</h2><p>Actividad reciente del área de ventas</p></div>
            </div>
            <div className="table-wrapper">
                <table className="orders-table">
                    <thead><tr><th>ID</th><th>Cliente</th><th>Canal</th><th>Total</th><th>Estado</th></tr></thead>
                    <tbody>
                        {loading ? (
                            <tr><td colSpan="5">Cargando órdenes...</td></tr>
                        ) : orders.length === 0 ? (
                            <tr><td colSpan="5">No hay órdenes registradas.</td></tr>
                        ) : orders.map((order) => (
                            <tr key={order.idOrdenVenta}>
                                <td>#{String(order.idOrdenVenta).padStart(3, "0")}</td>
                                <td>{order.cliente || "Sin cliente"}</td>
                                <td>{order.canalPedido}</td>
                                <td>S/ {Number(order.total || 0).toFixed(2)}</td>
                                <td><span className={`badge estado estado-${String(order.estado || "").toLowerCase()}`}>{order.estado}</span></td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </section>
    );
}
