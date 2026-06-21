import { useEffect, useState } from "react";
import {
    listarVerificacionesProductos,
    confirmarVerificacionProducto,
    observarVerificacionProducto,
} from "../../../services/despacho_almacen/almacenService";
import "./DespachoPages.css";

function VerificarProductos() {
    const [verificaciones, setVerificaciones] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [estadoFiltro, setEstadoFiltro] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarVerificaciones();
    }, []);

    const cargarVerificaciones = async () => {
        try {
            setLoading(true);
            const data = await listarVerificacionesProductos();
            setVerificaciones(data);
        } catch (error) {
            console.error("Error al cargar verificaciones:", error);
        } finally {
            setLoading(false);
        }
    };

    const confirmar = async (idVerificacion) => {
        try {
            await confirmarVerificacionProducto(idVerificacion);
            await cargarVerificaciones();
        } catch (error) {
            console.error("Error al confirmar:", error);
            alert(error.message);
        }
    };

    const observar = async (idVerificacion) => {
        try {
            await observarVerificacionProducto(idVerificacion);
            await cargarVerificaciones();
        } catch (error) {
            console.error("Error al observar:", error);
            alert(error.message);
        }
    };

    const verificacionesFiltradas = verificaciones.filter((item) => {
        const textoBusqueda = `
            ${item.idPedidoCompra ?? ""}
            ${item.producto ?? ""}
        `.toLowerCase();

        const coincideBusqueda = textoBusqueda.includes(busqueda.toLowerCase());

        const coincideEstado =
            estadoFiltro === "" || item.estado === estadoFiltro;

        return coincideBusqueda && coincideEstado;
    });

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
                        <input
                            className="despacho-search"
                            placeholder="Buscar pedido o producto"
                            value={busqueda}
                            onChange={(e) => setBusqueda(e.target.value)}
                        />

                        <select
                            className="despacho-select"
                            value={estadoFiltro}
                            onChange={(e) => setEstadoFiltro(e.target.value)}
                        >
                            <option value="">Todos los estados</option>
                            <option value="CONFORME">Conforme</option>
                            <option value="OBSERVADO">Observado</option>
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
                            {loading ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        Cargando verificaciones...
                                    </td>
                                </tr>
                            ) : verificacionesFiltradas.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        No se encontraron verificaciones
                                    </td>
                                </tr>
                            ) : (
                                verificacionesFiltradas.map((item) => (
                                    <tr key={item.idVerificacion}>
                                        <td>{item.producto}</td>
                                        <td>{item.cantidadPedida}</td>
                                        <td>{item.cantidadRecibida}</td>
                                        <td>{item.cantidadRecibida - item.cantidadPedida}</td>
                                        <td>
                                            <span
                                                className={
                                                    item.estado === "CONFORME"
                                                        ? "estado conforme"
                                                        : "estado observado"
                                                }
                                            >
                                                {item.estado}
                                            </span>
                                        </td>
                                        <td>
                                            <div className="despacho-actions">
                                                <button onClick={() => confirmar(item.idVerificacion)}>
                                                    Confirmar
                                                </button>

                                                <button
                                                    className="danger"
                                                    onClick={() => observar(item.idVerificacion)}
                                                >
                                                    Observar
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

export default VerificarProductos;