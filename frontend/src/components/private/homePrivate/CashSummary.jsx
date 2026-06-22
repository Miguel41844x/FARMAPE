import { useEffect, useState } from "react";
import { listarOrdenesPendientesCaja } from "../../../services/caja/cajaService";
import "./ordersTable.css";

export default function CashSummary() {
    const [ordenes, setOrdenes] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        let activo = true;
        listarOrdenesPendientesCaja()
            .then((data) => activo && setOrdenes(data))
            .catch((error) => console.error("Error al cargar caja:", error))
            .finally(() => activo && setLoading(false));
        return () => { activo = false; };
    }, []);

    const totalPendiente = ordenes.reduce((total, orden) => total + Number(orden.total || 0), 0);

    return (
        <section className="orders-container">
            <div className="orders-header">
                <div><h2>Órdenes por cobrar</h2><p>{loading ? "Consultando caja..." : `${ordenes.length} órdenes · S/ ${totalPendiente.toFixed(2)}`}</p></div>
            </div>
            {!loading && ordenes.length === 0 && <p>No hay órdenes pendientes en este momento.</p>}
        </section>
    );
}
