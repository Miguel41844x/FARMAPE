import { useState, useEffect } from "react";
import "./pagoComprobante.css";

const PagoComprobante = ({ orden, procesarPago }) => {
    const [metodoPago, setMetodoPago] = useState("Efectivo");
    const [montoRecibido, setMontoRecibido] = useState("");
    const [vuelto, setVuelto] = useState(0);

    useEffect(() => {
        if (!orden) {
            setMontoRecibido("");
            setVuelto(0);
            return;
        }

        if (orden.estado === "Pagado" || orden.estado === "Entregado" || orden.estado === "Anulado") {
            setMetodoPago(orden.metodoPago || "Efectivo");
            setMontoRecibido(orden.montoRecibido || orden.total);
            
            const recibidoFijo = parseFloat(orden.montoRecibido) || orden.total;
            const calculoVueltoFijo = recibidoFijo - orden.total;
            setVuelto(calculoVueltoFijo > 0 ? calculoVueltoFijo : 0);
            return;
        }

        if (metodoPago !== "Efectivo") {
            setMontoRecibido(orden.total);
            setVuelto(0);
        } else {
            const recibido = parseFloat(montoRecibido) || 0;
            const calculoVuelto = recibido - orden.total;
            setVuelto(calculoVuelto > 0 ? calculoVuelto : 0);
        }
    }, [montoRecibido, metodoPago, orden]);

    useEffect(() => {
        if (orden && (orden.estado === "Pagado" || orden.estado === "Entregado" || orden.estado === "Anulado")) {
            setMetodoPago(orden.metodoPago || "Efectivo");
            setMontoRecibido(orden.montoRecibido || orden.total);
        } else {
            setMontoRecibido("");
            setMetodoPago("Efectivo");
        }
    }, [orden]);

    const handleSubmit = (e) => {
        e.preventDefault();
        const montoNum = parseFloat(montoRecibido) || 0;
        if (metodoPago === "Efectivo" && montoNum < 0) {
            alert("El monto recibido no puede ser menor a cero (0)");
            setMontoRecibido("");
            return;
        }
        procesarPago({ metodoPago, montoRecibido });
    };

    if (!orden) {
        return (
            <div className="pago-card placeholder-state">
                <h2>Registrar pago</h2>
                <p className="pago-placeholder">Selecciona una orden de venta para registrar el pago.</p>
            </div>
        );
    }

    const yaFinalizado = orden.estado === "Pagado" || orden.estado === "Entregado" || orden.estado === "Anulado";
    const montoNum = parseFloat(montoRecibido) || 0;
    const botonDeshabilitado = yaFinalizado || (metodoPago === "Efectivo" && (montoRecibido === "" || montoNum < orden.total));

    return (
        <div className="pago-card">
            <h2>Registrar pago</h2>
            <form className="pago-form" onSubmit={handleSubmit}>
                <div className="pago-field">
                    <label htmlFor="metodoPago">Método de Pago</label>
                    <select 
                        id="metodoPago"
                        value={metodoPago} 
                        onChange={(e) => setMetodoPago(e.target.value)}
                        disabled={yaFinalizado} 
                    >
                        <option value="Efectivo">Efectivo</option>
                        <option value="Yape">Yape</option>
                        <option value="Tarjeta">Tarjeta</option>
                    </select>
                </div>

                {metodoPago === "Efectivo" && (
                    <div className="pago-field">
                        <label htmlFor="montoRecibido">Monto Recibido</label>
                        <div className="input-with-prefix">
                            <span className="currency-prefix">S/</span>
                            <input
                                id="montoRecibido"
                                type="number"
                                step="0.10"
                                min="0"
                                placeholder="0.00"
                                value={montoRecibido}
                                onChange={(e) => setMontoRecibido(e.target.value)}
                                required
                                disabled={yaFinalizado} 
                            />
                        </div>
                    </div>
                )}

                {metodoPago === "Efectivo" && (
                    <div className="pago-vuelto-container">
                        <span className="vuelto-label">Vuelto estimado:</span>
                        <strong className="vuelto-monto">
                            S/ {vuelto.toFixed(2)}
                        </strong>
                    </div>
                )}

                <button 
                    type="submit" 
                    className="btn-emitir-comprobante"
                    disabled={botonDeshabilitado}
                >
                    {orden.estado === "Anulado" ? "Venta Anulada" : `Emitir ${orden.tipoComprobante}`}
                </button>
            </form>
        </div>
    );
};

export default PagoComprobante;