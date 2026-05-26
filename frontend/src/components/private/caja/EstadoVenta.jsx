import { useState, useEffect } from "react";
import "./estadoVenta.css";

const EstadoVenta = ({ orden, actualizarEstado }) => {
    const [estado, setEstado] = useState("");

    useEffect(() => {
        if (orden) {
            setEstado(orden.estado || "Pendiente");
        }
    }, [orden]);

    if (!orden) {
        return (
            <div className="estado-card placeholder-state">
                <h2>Detalle de la Orden</h2>
                <p className="estado-placeholder">No hay ninguna orden activa para inspeccionar.</p>
            </div>
        );
    }

    const esPagado = orden.estado === "Pagado";

    return (
        <div className="estado-card">
            <h2>Detalles de Venta: {orden.codigoVenta || orden.id}</h2>

            <div className="estado-productos-resumen">
                <h3>Medicamentos cargados:</h3>
                <ul className="caja-productos-lista">
                    {orden.productos?.map((prod, index) => (
                        <li key={index} className="caja-producto-item">
                            <span className="producto-nombre">
                                {prod.nombre} <span className="producto-cantidad">x{prod.cantidad}</span>
                            </span>
                            <strong className="producto-subtotal">
                                S/ {prod.subtotal.toFixed(2)}
                            </strong>
                        </li>
                    ))}
                </ul>
            </div>

            <div className="estado-form-caja">
                <label htmlFor="estadoProceso">Estado Actual del Proceso</label>
                <select 
                    id="estadoProceso"
                    value={estado} 
                    onChange={(e) => setEstado(e.target.value)}
                    disabled={!esPagado}
                    className={!esPagado ? "select-bloqueado" : "select-editable"}
                >
                    {orden.estado === "Pendiente" ? (
                        <option value="Pendiente">Pendiente</option>
                    ) : (
                        <>
                            <option value="Pagado">Pagado</option>
                            <option value="Entregado">Entregado</option>
                            <option value="Anulado">Anulado</option>
                        </>
                    )}
                </select>

                <button
                    className="btn-emitir-comprobante"
                    disabled={!esPagado || estado === orden.estado}
                    onClick={() => {
                        if (estado !== orden.estado) {
                            actualizarEstado(estado);
                        }
                    }}
                    style={{ marginTop: "12px", cursor: esPagado && estado !== orden.estado ? "pointer" : "not-allowed" }}
                >
                    Cambiar estado
                </button>
            </div>
        </div>
    );
};

export default EstadoVenta;