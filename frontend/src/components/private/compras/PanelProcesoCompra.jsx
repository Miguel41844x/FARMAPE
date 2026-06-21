import { useEffect, useState } from "react";
import { obtenerProveedores } from "../../../services/compras/proveedorService";
import { obtenerProductos } from "../../../services/mantenimiento/productoService";
import FormularioProcesoCompra from "./FormularioProcesoCompra";
import "./panelProcesoCompra.css";

const normalizarLista = (data) => Array.isArray(data) ? data : data?.content || [];

function PanelProcesoCompra({ configuracion }) {
    const [registros, setRegistros] = useState([]);
    const [proveedores, setProveedores] = useState([]);
    const [productos, setProductos] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [mostrarFormulario, setMostrarFormulario] = useState(false);

    useEffect(() => {
        let activo = true;

        Promise.allSettled([configuracion.listar(), obtenerProveedores(), obtenerProductos()])
            .then(([resultadoRegistros, resultadoProveedores, resultadoProductos]) => {
                if (!activo) return;

                if (resultadoRegistros.status === "fulfilled") {
                    setRegistros(normalizarLista(resultadoRegistros.value));
                } else {
                    setError(resultadoRegistros.reason.message);
                }

                if (resultadoProveedores.status === "fulfilled") {
                    setProveedores(normalizarLista(resultadoProveedores.value));
                }

                if (resultadoProductos.status === "fulfilled") {
                    setProductos(normalizarLista(resultadoProductos.value));
                }
            })
            .finally(() => {
                if (activo) setLoading(false);
            });

        return () => {
            activo = false;
        };
    }, [configuracion]);

    const recargar = async () => {
        const data = await configuracion.listar();
        setRegistros(normalizarLista(data));
    };

    const guardar = async (payload) => {
        try {
            await configuracion.crear(payload);
            await recargar();
            setMostrarFormulario(false);
        } catch (err) {
            setError(err.message);
            throw err;
        }
    };

    return (
        <main className="compra-process-page">
            <header className="compra-process-header">
                <div>
                    <h1>{configuracion.titulo}</h1>
                    <p>{configuracion.descripcion}</p>
                </div>
                <button type="button" onClick={() => setMostrarFormulario(true)}>
                    {configuracion.accion}
                </button>
            </header>

            {error && <div className="compra-error" role="alert">{error}</div>}

            <section className="compra-table-section">
                <header>
                    <div>
                        <h2>{configuracion.tituloTabla}</h2>
                        <span>{registros.length} registros encontrados</span>
                    </div>
                </header>

                <div className="compra-table-wrapper">
                    <table className="compra-table">
                        <thead>
                            <tr>{configuracion.columnas.map((columna) => <th key={columna.key}>{columna.label}</th>)}</tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan={configuracion.columnas.length} className="compra-empty">Cargando registros...</td></tr>
                            ) : registros.length > 0 ? registros.map((registro, index) => (
                                <tr key={registro.id || registro.idOrdenCompra || registro.idFacturaProveedor || index}>
                                    {configuracion.columnas.map((columna) => (
                                        <td key={columna.key}>{columna.render ? columna.render(registro) : registro[columna.key] || "-"}</td>
                                    ))}
                                </tr>
                            )) : (
                                <tr><td colSpan={configuracion.columnas.length} className="compra-empty">No hay registros para mostrar</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </section>

            {mostrarFormulario && (
                <div className="compra-modal-overlay" onMouseDown={() => setMostrarFormulario(false)}>
                    <div className="compra-modal-content" onMouseDown={(event) => event.stopPropagation()}>
                        <FormularioProcesoCompra
                            tipo={configuracion.tipo}
                            proveedores={proveedores}
                            productos={productos}
                            onCancel={() => setMostrarFormulario(false)}
                            onSave={guardar}
                        />
                    </div>
                </div>
            )}
        </main>
    );
}

export default PanelProcesoCompra;
