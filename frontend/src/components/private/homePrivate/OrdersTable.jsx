import { useEffect, useState } from "react";
import "./ordersTable.css";

export default function OrdersTable() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        obtenerUltimasOrdenes();
    }, []);

    const obtenerUltimasOrdenes = async () => {
        try {
            setLoading(true);

            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/ventas", {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("No se pudieron cargar las órdenes");
            }

            const data = await response.json();

            const ultimasOrdenes = data
                .sort((a, b) => b.idOrdenVenta - a.idOrdenVenta)
                .slice(0, 4);

            setOrders(ultimasOrdenes);
        } catch (error) {
            console.error("Error al cargar últimas órdenes:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="orders-container">
            <div className="orders-header">
                <h2>Últimas órdenes</h2>

                <button className="view-btn">
                    Ver todas las órdenes
                </button>
            </div>

            <div className="table-wrapper">
                <table className="orders-table">
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

                    <tbody>
                        {loading && (
                            <tr>
                                <td colSpan="6">
                                    Cargando últimas órdenes...
                                </td>
                            </tr>
                        )}

                        {!loading && orders.length === 0 && (
                            <tr>
                                <td colSpan="6">
                                    No hay órdenes registradas.
                                </td>
                            </tr>
                        )}

                        {!loading && orders.map((order) => (
                            <tr key={order.idOrdenVenta}>
                                <td className="id">
                                    #{String(order.idOrdenVenta).padStart(3, "0")}
                                </td>

                                <td className="cliente">
                                    {order.cliente || "Sin cliente"}
                                </td>

                                <td>
                                    <span className="badge canal">
                                        {order.canalPedido || "Sin canal"}
                                    </span>
                                </td>

                                <td className="total">
                                    S/ {Number(order.total || 0).toFixed(2)}
                                </td>

                                <td>
                                    <span className="badge estado">
                                        {order.estado || "Sin estado"}
                                    </span>
                                </td>

                                <td>
                                    <button
                                        className="details-btn"
                                        onClick={() => console.log("Orden:", order)}
                                    >
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