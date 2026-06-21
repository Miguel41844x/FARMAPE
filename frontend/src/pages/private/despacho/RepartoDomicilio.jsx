import { useEffect, useState } from "react";
import {
    listarRepartosDomicilio,
    marcarRepartoEntregado,
} from "../../../services/despacho_almacen/despachoService";
import "./DespachoPages.css";

function RepartoDomicilio() {
    const [repartos, setRepartos] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [estadoFiltro, setEstadoFiltro] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarRepartos();
    }, []);

    const cargarRepartos = async () => {
        try {
            setLoading(true);
            const data = await listarRepartosDomicilio();
            setRepartos(data);
        } catch (error) {
            console.error("Error al cargar repartos:", error);
        } finally {
            setLoading(false);
        }
    };

    const entregarReparto = async (idReparto) => {
        try {
            await marcarRepartoEntregado(idReparto);
            await cargarRepartos();
        } catch (error) {
            console.error("Error al marcar reparto:", error);
            alert(error.message);
        }
    };

    const repartosFiltrados = repartos.filter((reparto) => {
        const textoBusqueda = `
            ${reparto.idOrdenVenta ?? ""}
            ${reparto.cliente ?? ""}
            ${reparto.direccion ?? ""}
            ${reparto.repartidor ?? ""}
        `.toLowerCase();

        const coincideBusqueda = textoBusqueda.includes(busqueda.toLowerCase());

        const coincideEstado =
            estadoFiltro === "" || reparto.estado === estadoFiltro;

        return coincideBusqueda && coincideEstado;
    });

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
                            <option value="PENDIENTE">Pendiente</option>
                            <option value="EN_RUTA">En ruta</option>
                            <option value="ENTREGADO">Entregado</option>
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
                            {loading ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        Cargando repartos...
                                    </td>
                                </tr>
                            ) : repartosFiltrados.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        No se encontraron repartos
                                    </td>
                                </tr>
                            ) : (
                                repartosFiltrados.map((reparto) => (
                                    <tr key={reparto.idReparto}>
                                        <td>OV-{reparto.idOrdenVenta}</td>
                                        <td>{reparto.cliente}</td>
                                        <td>{reparto.direccion}</td>
                                        <td>{reparto.repartidor || "Sin asignar"}</td>
                                        <td>
                                            <span
                                                className={
                                                    reparto.estado === "ENTREGADO"
                                                        ? "estado entregado"
                                                        : reparto.estado === "EN_RUTA"
                                                            ? "estado ruta"
                                                            : "estado pendiente"
                                                }
                                            >
                                                {reparto.estado}
                                            </span>
                                        </td>
                                        <td>
                                            <div className="despacho-actions">
                                                <button
                                                    disabled={reparto.estado === "ENTREGADO"}
                                                    onClick={() => entregarReparto(reparto.idReparto)}
                                                >
                                                    {reparto.estado === "ENTREGADO"
                                                        ? "Entregado"
                                                        : "Marcar entregado"}
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

export default RepartoDomicilio;