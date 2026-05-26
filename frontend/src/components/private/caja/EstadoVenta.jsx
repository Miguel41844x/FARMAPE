import "./estadoVenta.css";

const EstadoVenta = ({ orden, resultadoPago }) => {
    if (!orden) {
        return (
            <div className="estado-card placeholder-state">
                <h2>Detalle de orden</h2>
                <p className="estado-placeholder">
                    Selecciona una orden para ver sus productos y datos de pago.
                </p>
            </div>
        );
    }

    const total = Number(orden.total || 0);

    return (
        <div className="estado-card">
            <h2>Detalle de orden #{orden.idOrdenVenta}</h2>

            <div className="detalle-orden-info">
                <div>
                    <span>Cliente</span>
                    <strong>{orden.cliente || "Cliente no registrado"}</strong>
                </div>

                <div>
                    <span>Empleado</span>
                    <strong>{orden.empleado || "No registrado"}</strong>
                </div>

                <div>
                    <span>Canal</span>
                    <strong>{orden.canalPedido}</strong>
                </div>

                <div>
                    <span>Estado</span>
                    <strong>{orden.estado}</strong>
                </div>
            </div>

            {orden.observacion && (
                <div className="detalle-observacion">
                    <span>Observación</span>
                    <p>{orden.observacion}</p>
                </div>
            )}

            <div className="estado-productos-resumen">
                <h3>Productos:</h3>

                <ul className="caja-productos-lista">
                    {orden.detalles?.map((prod) => {
                        const subtotal = Number(prod.subtotal || 0);

                        return (
                            <li key={prod.idDetalleVenta} className="caja-producto-item">
                                <span className="producto-nombre">
                                    {prod.producto}
                                    <span className="producto-cantidad">
                                        x{prod.cantidad}
                                    </span>
                                </span>

                                <strong className="producto-subtotal">
                                    S/ {subtotal.toFixed(2)}
                                </strong>
                            </li>
                        );
                    })}
                </ul>
            </div>

            <div className="detalle-total-caja">
                <span>Total</span>
                <strong>S/ {total.toFixed(2)}</strong>
            </div>

            {resultadoPago && (
                <div className="comprobante-box">
                    <h3>Comprobante emitido</h3>

                    <div className="detalle-orden-info">
                        <div>
                            <span>Tipo</span>
                            <strong>{resultadoPago.comprobante.tipoComprobante}</strong>
                        </div>

                        <div>
                            <span>Número</span>
                            <strong>
                                {resultadoPago.comprobante.serie}-{resultadoPago.comprobante.numero}
                            </strong>
                        </div>

                        <div>
                            <span>Método</span>
                            <strong>{resultadoPago.pago.metodoPago}</strong>
                        </div>

                        <div>
                            <span>Monto pagado</span>
                            <strong>
                                S/ {Number(resultadoPago.pago.montoPagado || 0).toFixed(2)}
                            </strong>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default EstadoVenta;