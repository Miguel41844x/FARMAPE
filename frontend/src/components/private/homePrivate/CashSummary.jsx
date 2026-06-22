import { useEffect, useState } from "react";
import { NavLink } from "react-router-dom";
import { FiArrowRight, FiClock, FiDollarSign } from "react-icons/fi";
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
                <div><span className="orders-eyebrow">Resumen operativo</span><h2>Órdenes por cobrar</h2><p>Estado actual de la cola de caja</p></div>
                <NavLink className="orders-action" to="/caja">Ir a caja <FiArrowRight /></NavLink>
            </div>
            <div className="cash-summary-grid">
                <article><FiClock /><div><span>Órdenes pendientes</span><strong>{loading ? "—" : ordenes.length}</strong></div></article>
                <article><FiDollarSign /><div><span>Monto por cobrar</span><strong>{loading ? "—" : `S/ ${totalPendiente.toFixed(2)}`}</strong></div></article>
            </div>
            {!loading && ordenes.length === 0 && <p className="orders-empty">La caja está al día. No hay órdenes pendientes en este momento.</p>}
        </section>
    );
}
