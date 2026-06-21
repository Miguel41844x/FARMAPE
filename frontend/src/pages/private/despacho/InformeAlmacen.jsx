import { useEffect, useState } from "react";
import { obtenerInformeAlmacen } from "../../../services/despacho_almacen/almacenService";
import "./DespachoPages.css";

function InformeAlmacen() {
    const [indicadores, setIndicadores] = useState([]);
    const [periodo, setPeriodo] = useState("HOY");
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        cargarInforme();
    }, [periodo]);

    const cargarInforme = async () => {
        try {
            setLoading(true);
            const data = await obtenerInformeAlmacen(periodo);
            setIndicadores(data);
        } catch (error) {
            console.error("Error al cargar informe:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="despacho-module">
            <div className="despacho-header">
                <h1>Informe de almacén</h1>
                <p>Consulta movimientos, ingresos, stock bajo y productos próximos a vencer.</p>
            </div>

            <section className="despacho-table-section">
                <div className="despacho-table-header">
                    <div>
                        <h2>Resumen de almacén</h2>
                        <span>Indicadores principales del inventario</span>
                    </div>

                    <div className="despacho-filters">
                        <select
                            className="despacho-select"
                            value={periodo}
                            onChange={(e) => setPeriodo(e.target.value)}
                        >
                            <option value="HOY">Hoy</option>
                            <option value="SEMANA">Esta semana</option>
                            <option value="MES">Este mes</option>
                        </select>
                    </div>
                </div>

                <div className="despacho-table-wrapper">
                    <table className="despacho-table">
                        <thead>
                            <tr>
                                <th>Indicador</th>
                                <th>Cantidad</th>
                                <th>Detalle</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>

                        <tbody>
                            {loading ? (
                                <tr>
                                    <td colSpan="4" className="usuarios-empty">
                                        Cargando informe...
                                    </td>
                                </tr>
                            ) : indicadores.length === 0 ? (
                                <tr>
                                    <td colSpan="4" className="usuarios-empty">
                                        No hay información disponible
                                    </td>
                                </tr>
                            ) : (
                                indicadores.map((item) => (
                                    <tr key={item.idIndicador}>
                                        <td>{item.indicador}</td>
                                        <td>{item.cantidad}</td>
                                        <td>{item.detalle}</td>
                                        <td>
                                            <div className="despacho-actions">
                                                <button>Ver detalle</button>
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

export default InformeAlmacen;