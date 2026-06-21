import { useEffect, useState } from "react";
import {
    listarOrdenesTienda,
    entregarOrdenTienda,
} from "../../../services/despacho_almacen/despachoService";
import "./DespachoPages.css";

function EntregaTienda() {
    const [ordenes, setOrdenes] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [estadoFiltro, setEstadoFiltro] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarOrdenes();
    }, []);

    const cargarOrdenes = async () => {
        try {
            setLoading(true);
            const data = await listarOrdenesTienda();
            setOrdenes(data);
        } catch (error) {
            console.error("Error al cargar órdenes:", error);
        } finally {
            setLoading(false);
        }
    };

    const entregarOrden = async (idOrdenVenta) => {
        try {
            await entregarOrdenTienda(idOrdenVenta);
            await cargarOrdenes();
        } catch (error) {
            console.error("Error al entregar orden:", error);
            alert(error.message);
        }
    };

    const ordenesFiltradas = ordenes.filter((orden) => {
        const textoBusqueda = `${orden.idOrdenVenta} ${orden.cliente ?? ""}`.toLowerCase();

        const coincideBusqueda = textoBusqueda.includes(busqueda.toLowerCase());

        const coincideEstado =
            estadoFiltro === "" || orden.estado === estadoFiltro;

        return coincideBusqueda && coincideEstado;
    });

    return (
        <div className="despacho-module">
            <div className="despacho-header">
                <h1>Entrega en tienda</h1>
                <p>Gestiona la entrega de órdenes pagadas al cliente.</p>
            </div>

            <section className="despacho-table-section">
                <div className="despacho-table-header">
                    <div>
                        <h2>Órdenes para despacho</h2>
                        <span>Listado de órdenes listas para entregar</span>
                    </div>

                    <div className="despacho-filters">
                        <input
                            className="despacho-search"
                            placeholder="Buscar por orden o cliente"
                            value={busqueda}
                            onChange={(e) => setBusqueda(e.target.value)}
                        />

                        <select
                            className="despacho-select"
                            value={estadoFiltro}
                            onChange={(e) => setEstadoFiltro(e.target.value)}
                        >
                            <option value="">Todos los estados</option>
                            <option value="PAGADA">Pagado</option>
                            <option value="ENTREGADA">Entregado</option>
                        </select>
                    </div>
                </div>

                <div className="despacho-table-wrapper">
                    <table className="despacho-table">
                        <thead>
                            <tr>
                                <th>Orden</th>
                                <th>Cliente</th>
                                <th>Fecha</th>
                                <th>Total</th>
                                <th>Estado</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        Cargando órdenes...
                                    </td>
                                </tr>
                            ) : ordenesFiltradas.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        No se encontraron órdenes
                                    </td>
                                </tr>
                            ) : (
                                ordenesFiltradas.map((orden) => (
                                    <tr key={orden.idOrdenVenta}>
                                        <td>OV-{orden.idOrdenVenta}</td>

                                        <td>{orden.cliente}</td>

                                        <td>
                                            {orden.fechaOrden
                                                ? new Date(orden.fechaOrden).toLocaleDateString("es-PE")
                                                : "-"}
                                        </td>

                                        <td>S/ {Number(orden.total).toFixed(2)}</td>

                                        <td>
                                            <span
                                                className={
                                                    orden.estado === "ENTREGADA"
                                                        ? "estado entregado"
                                                        : "estado pagado"
                                                }
                                            >
                                                {orden.estado}
                                            </span>
                                        </td>

                                        <td>
                                            <div className="despacho-actions">
                                                <button
                                                    disabled={orden.estado === "ENTREGADA"}
                                                    onClick={() => entregarOrden(orden.idOrdenVenta)}
                                                >
                                                    {orden.estado === "ENTREGADA"
                                                        ? "Entregado"
                                                        : "Entregar"}
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    );
}

export default EntregaTienda;