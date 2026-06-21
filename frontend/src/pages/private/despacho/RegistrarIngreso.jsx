import { useEffect, useState } from "react";
import {
    listarIngresosAlmacen,
    registrarIngresoAlmacen,
} from "../../../services/despacho_almacen/almacenService";
import "./DespachoPages.css";

function RegistrarIngreso() {
    const [ingresos, setIngresos] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [proveedorFiltro, setProveedorFiltro] = useState("");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarIngresos();
    }, []);

    const cargarIngresos = async () => {
        try {
            setLoading(true);
            const data = await listarIngresosAlmacen();
            setIngresos(data);
        } catch (error) {
            console.error("Error al cargar ingresos:", error);
        } finally {
            setLoading(false);
        }
    };

    const registrarIngreso = async (ingreso) => {
        try {
            await registrarIngresoAlmacen({
                idProducto: ingreso.idProducto,
                cantidad: ingreso.cantidad,
                lote: ingreso.lote,
                fechaVencimiento: ingreso.fechaVencimiento,
                idProveedor: ingreso.idProveedor,
            });

            await cargarIngresos();
        } catch (error) {
            console.error("Error al registrar ingreso:", error);
            alert(error.message);
        }
    };

    const ingresosFiltrados = ingresos.filter((ingreso) => {
        const textoBusqueda = `
            ${ingreso.producto ?? ""}
            ${ingreso.lote ?? ""}
            ${ingreso.proveedor ?? ""}
        `.toLowerCase();

        const coincideBusqueda = textoBusqueda.includes(busqueda.toLowerCase());

        const coincideProveedor =
            proveedorFiltro === "" || ingreso.proveedor === proveedorFiltro;

        return coincideBusqueda && coincideProveedor;
    });

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
                        <input
                            className="despacho-search"
                            placeholder="Buscar producto o lote"
                            value={busqueda}
                            onChange={(e) => setBusqueda(e.target.value)}
                        />

                        <select
                            className="despacho-select"
                            value={proveedorFiltro}
                            onChange={(e) => setProveedorFiltro(e.target.value)}
                        >
                            <option value="">Todos los proveedores</option>
                            <option value="Farma Perú">Farma Perú</option>
                            <option value="Distribuidora Andina">Distribuidora Andina</option>
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
                            {loading ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        Cargando ingresos...
                                    </td>
                                </tr>
                            ) : ingresosFiltrados.length === 0 ? (
                                <tr>
                                    <td colSpan="6" className="usuarios-empty">
                                        No se encontraron ingresos
                                    </td>
                                </tr>
                            ) : (
                                ingresosFiltrados.map((ingreso) => (
                                    <tr key={ingreso.idIngreso}>
                                        <td>{ingreso.producto}</td>
                                        <td>{ingreso.cantidad}</td>
                                        <td>{ingreso.lote}</td>
                                        <td>
                                            {ingreso.fechaVencimiento
                                                ? new Date(ingreso.fechaVencimiento).toLocaleDateString("es-PE")
                                                : "-"}
                                        </td>
                                        <td>{ingreso.proveedor}</td>
                                        <td>
                                            <div className="despacho-actions">
                                                <button onClick={() => registrarIngreso(ingreso)}>
                                                    Registrar
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

export default RegistrarIngreso;