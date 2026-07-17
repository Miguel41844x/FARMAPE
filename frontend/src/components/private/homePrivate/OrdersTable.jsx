import { useEffect, useState } from "react";
import { obtenerUltimasOrdenes, rechazarVenta } from "../../../services/ventas/ventaService";
import "./ordersTable.css";

export default function OrdersTable() {
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [rechazandoId, setRechazandoId] = useState(null);

    useEffect(() => {
        let activo = true;
        obtenerUltimasOrdenes()
            .then((data) => activo && setOrders(data))
            .catch((error) => console.error("Error al cargar ultimas ordenes:", error))
            .finally(() => activo && setLoading(false));
        return () => { activo = false; };
    }, []);

    const puedeRechazar = (estado) => ["pendiente", "confirmada"].includes(String(estado || "").toLowerCase());

    const manejarRechazo = async (order) => {
        const confirmar = window.confirm(`Deseas rechazar la orden #${order.idOrdenVenta}?`);
        if (!confirmar) return;

        try {
            setRechazandoId(order.idOrdenVenta);
            const ordenActualizada = await rechazarVenta(order.idOrdenVenta);

            setOrders((actuales) =>
                actuales.map((actual) =>
                    actual.idOrdenVenta === order.idOrdenVenta
                        ? { ...actual, ...ordenActualizada }
                        : actual
                )
            );
        } catch (error) {
            console.error("Error al rechazar orden:", error);
            alert(error.message);
        } finally {
            setRechazandoId(null);
        }
    };

    return (
        <section className="orders-container">
            <div className="orders-header">
                <div><h2>Ultimas ordenes</h2><p>Actividad reciente del area de ventas</p></div>
            </div>
            <div className="table-wrapper">
                <table className="orders-table">
                    <thead><tr><th>ID</th><th>Cliente</th><th>Canal</th><th>Total</th><th>Estado</th><th>Acciones</th></tr></thead>
                    <tbody>
                        {loading ? (
                            <tr><td colSpan="6">Cargando ordenes...</td></tr>
                        ) : orders.length === 0 ? (
                            <tr><td colSpan="6">No hay ordenes registradas.</td></tr>
                        ) : orders.map((order) => (
                            <tr key={order.idOrdenVenta}>
                                <td>#{String(order.idOrdenVenta).padStart(3, "0")}</td>
                                <td>{order.cliente || "Sin cliente"}</td>
                                <td>{order.canalPedido}</td>
                                <td>S/ {Number(order.total || 0).toFixed(2)}</td>
                                <td><span className={`badge estado estado-${String(order.estado || "").toLowerCase()}`}>{order.estado}</span></td>
                                <td>
                                    {puedeRechazar(order.estado) ? (
                                        <button
                                            type="button"
                                            className="orders-reject-button"
                                            onClick={() => manejarRechazo(order)}
                                            disabled={rechazandoId === order.idOrdenVenta}
                                        >
                                            {rechazandoId === order.idOrdenVenta ? "Procesando" : "Rechazar"}
                                        </button>
                                    ) : (
                                        <span className="orders-no-action">-</span>
                                    )}
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </section>
    );
}
