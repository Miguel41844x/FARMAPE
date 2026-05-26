import { useEffect, useState } from "react";
import "./pagoComprobante.css";

const PagoComprobante = ({ orden, procesarPago, loadingPago }) => {
    const [metodoPago, setMetodoPago] = useState("Efectivo");
    const [tipoComprobante, setTipoComprobante] = useState("Boleta");
    const [montoPagado, setMontoPagado] = useState("");
    const [vuelto, setVuelto] = useState(0);

    useEffect(() => {
        if (!orden) {
            setMetodoPago("Efectivo");
            setTipoComprobante("Boleta");
            setMontoPagado("");
            setVuelto(0);
            return;
        }

        if (metodoPago !== "Efectivo") {
            setMontoPagado(String(Number(orden.total || 0)));
            setVuelto(0);
            return;
        }

        const recibido = Number(montoPagado || 0);
        const total = Number(orden.total || 0);
        const calculo = recibido - total;

        setVuelto(calculo > 0 ? calculo : 0);
    }, [orden, metodoPago, montoPagado]);

    const handleSubmit = (e) => {
        e.preventDefault();

        if (!orden) {
            alert("Selecciona una orden primero");
            return;
        }

        const total = Number(orden.total || 0);
        const monto = metodoPago === "Efectivo"
            ? Number(montoPagado || 0)
            : total;

        if (monto <= 0) {
            alert("El monto pagado debe ser mayor a 0");
            return;
        }

        if (monto < total) {
            alert("El monto pagado no cubre el total de la orden");
            return;
        }

        procesarPago({
            montoPagado: monto,
            metodoPago,
            tipoComprobante,
        });
    };

    if (!orden) {
        return (
            <div className="pago-card placeholder-state">
                <h2>Registrar pago</h2>
                <p className="pago-placeholder">
                    Selecciona una orden pendiente para registrar el pago.
                </p>
            </div>
        );
    }

    const total = Number(orden.total || 0);
    const monto = Number(montoPagado || 0);
    const botonDeshabilitado =
        loadingPago ||
        orden.estado !== "Pendiente" ||
        (metodoPago === "Efectivo" && (montoPagado === "" || monto < total));

    return (
        <div className="pago-card">
            <h2>Registrar pago</h2>

            <div className="pago-resumen-orden">
                <span>Total de la orden</span>
                <strong>S/ {total.toFixed(2)}</strong>
            </div>

            <form className="pago-form" onSubmit={handleSubmit}>
                <div className="pago-field">
                    <label htmlFor="tipoComprobante">Tipo de comprobante</label>
                    <select
                        id="tipoComprobante"
                        value={tipoComprobante}
                        onChange={(e) => setTipoComprobante(e.target.value)}
                        disabled={loadingPago || orden.estado !== "Pendiente"}
                    >
                        <option value="Boleta">Boleta</option>
                        <option value="Factura">Factura</option>
                    </select>
                </div>

                <div className="pago-field">
                    <label htmlFor="metodoPago">Método de pago</label>
                    <select
                        id="metodoPago"
                        value={metodoPago}
                        onChange={(e) => setMetodoPago(e.target.value)}
                        disabled={loadingPago || orden.estado !== "Pendiente"}
                    >
                        <option value="Efectivo">Efectivo</option>
                        <option value="Tarjeta">Tarjeta</option>
                        <option value="Yape">Yape</option>
                        <option value="Plin">Plin</option>
                        <option value="Transferencia">Transferencia</option>
                    </select>
                </div>

                {metodoPago === "Efectivo" && (
                    <div className="pago-field">
                        <label htmlFor="montoPagado">Monto recibido</label>
                        <div className="input-with-prefix">
                            <span className="currency-prefix">S/</span>
                            <input
                                id="montoPagado"
                                type="number"
                                step="0.10"
                                min="0"
                                placeholder="0.00"
                                value={montoPagado}
                                onChange={(e) => setMontoPagado(e.target.value)}
                                required
                                disabled={loadingPago || orden.estado !== "Pendiente"}
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

                {metodoPago !== "Efectivo" && (
                    <div className="pago-vuelto-container">
                        <span className="vuelto-label">Monto a cobrar:</span>
                        <strong className="vuelto-monto">
                            S/ {total.toFixed(2)}
                        </strong>
                    </div>
                )}

                <button
                    type="submit"
                    className="btn-emitir-comprobante"
                    disabled={botonDeshabilitado}
                >
                    {loadingPago ? "Procesando..." : `Emitir ${tipoComprobante}`}
                </button>
            </form>
        </div>
    );
};

export default PagoComprobante;