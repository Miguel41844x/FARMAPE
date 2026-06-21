import { useEffect, useState } from "react";
import ProveedorForm from "../../../components/private/mantenimiento/proveedores/ProveedorForm";
import {
    eliminarProveedor,
    obtenerProveedores,
} from "../../../services/compras/proveedorService";
import "./proveedores.css";

const normalizarLista = (data) => Array.isArray(data) ? data : data?.content || [];

function Proveedores() {
    const [proveedores, setProveedores] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [formularioAbierto, setFormularioAbierto] = useState(false);
    const [proveedorEditando, setProveedorEditando] = useState(null);

    useEffect(() => {
        let activo = true;

        obtenerProveedores()
            .then((data) => {
                if (activo) setProveedores(normalizarLista(data));
            })
            .catch((err) => {
                if (activo) setError(err.message);
            })
            .finally(() => {
                if (activo) setLoading(false);
            });

        return () => {
            activo = false;
        };
    }, []);

    const cargarProveedores = async () => {
        try {
            setLoading(true);
            setError("");
            const data = await obtenerProveedores();
            setProveedores(normalizarLista(data));
        } catch (err) {
            setError(err.message);
        } finally {
            setLoading(false);
        }
    };

    const abrirFormulario = (proveedor = null) => {
        setProveedorEditando(proveedor);
        setFormularioAbierto(true);
    };

    const cerrarFormulario = () => {
        setProveedorEditando(null);
        setFormularioAbierto(false);
    };

    const borrarProveedor = async (proveedor) => {
        if (!confirm(`¿Deseas eliminar a ${proveedor.razonSocial}?`)) return;

        try {
            await eliminarProveedor(proveedor.idProveedor);
            await cargarProveedores();
        } catch (err) {
            setError(err.message);
        }
    };

    const texto = busqueda.trim().toLocaleLowerCase("es");
    const proveedoresFiltrados = proveedores.filter((proveedor) =>
        [proveedor.ruc, proveedor.razonSocial, proveedor.email, proveedor.tipoProveedor]
            .some((valor) => String(valor ?? "").toLocaleLowerCase("es").includes(texto))
    );

    return (
        <main className="proveedores-container">
            <header className="proveedores-header">
                <div>
                    <h1>Gestión de proveedores</h1>
                    <p>Registra y administra los proveedores de la farmacia.</p>
                </div>
                <button type="button" className="proveedores-create-btn" onClick={() => abrirFormulario()}>
                    Crear proveedor
                </button>
            </header>

            {error && <div className="proveedores-error" role="alert">{error}</div>}

            <section className="proveedores-table-section">
                <div className="proveedores-table-header">
                    <div>
                        <h2>Proveedores registrados</h2>
                        <span>{proveedoresFiltrados.length} proveedores encontrados</span>
                    </div>
                    <input
                        className="proveedores-search"
                        type="search"
                        value={busqueda}
                        onChange={(event) => setBusqueda(event.target.value)}
                        placeholder="Buscar por RUC, nombre, correo o tipo"
                    />
                </div>

                <div className="proveedores-table-wrapper">
                    <table className="proveedores-table">
                        <thead>
                            <tr>
                                <th>RUC</th><th>Razón social</th><th>Teléfono</th><th>Email</th><th>Tipo</th><th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            {loading ? (
                                <tr><td colSpan="6" className="proveedores-empty">Cargando proveedores...</td></tr>
                            ) : proveedoresFiltrados.length > 0 ? proveedoresFiltrados.map((proveedor) => (
                                <tr key={proveedor.idProveedor}>
                                    <td>{proveedor.ruc}</td>
                                    <td><strong>{proveedor.razonSocial}</strong></td>
                                    <td>{proveedor.telefono || "-"}</td>
                                    <td>{proveedor.email || "-"}</td>
                                    <td><span className="proveedores-status">{proveedor.tipoProveedor}</span></td>
                                    <td>
                                        <div className="proveedores-actions">
                                            <button type="button" className="proveedores-edit-btn" onClick={() => abrirFormulario(proveedor)}>Editar</button>
                                            <button type="button" className="proveedores-delete-btn" onClick={() => borrarProveedor(proveedor)}>Eliminar</button>
                                        </div>
                                    </td>
                                </tr>
                            )) : (
                                <tr><td colSpan="6" className="proveedores-empty">No se encontraron proveedores</td></tr>
                            )}
                        </tbody>
                    </table>
                </div>
            </section>

            {formularioAbierto && (
                <div className="proveedores-modal-overlay" onMouseDown={cerrarFormulario}>
                    <div className="proveedores-modal-content" onMouseDown={(event) => event.stopPropagation()}>
                        <ProveedorForm
                            key={proveedorEditando?.idProveedor || "nuevo"}
                            proveedorEditando={proveedorEditando}
                            cerrarFormulario={cerrarFormulario}
                            obtenerProveedores={cargarProveedores}
                        />
                    </div>
                </div>
            )}
        </main>
    );
}

export default Proveedores;
