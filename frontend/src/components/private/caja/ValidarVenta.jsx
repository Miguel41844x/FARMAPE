import "./validarVenta.css";

const ValidarVenta = ({ ordenes = [], ordenSeleccionada, seleccionarOrden, loading }) => {
    return (
        <section className="validar-card">
            <div className="caja-section-header">
                <h2>Órdenes confirmadas</h2>
                <span>{ordenes.length} órdenes</span>
            </div>

            {loading && (
                <p className="caja-empty-message">Cargando órdenes confirmadas...</p>
            )}

            {!loading && ordenes.length === 0 && (
                <p className="caja-empty-message">
                    No hay órdenes confirmadas por cobrar.
                </p>
            )}

            <div className="ventas-list">
                {ordenes.map((venta) => {
                    const esActivo =
                        ordenSeleccionada?.idOrdenVenta === venta.idOrdenVenta;

                    const total = Number(venta.total || 0);

                    return (
                        <div
                            className={`venta-item ${esActivo ? "activo" : ""}`}
                            key={venta.idOrdenVenta}
                        >
                            <div className="venta-item-info">
                                <h3>Orden #{venta.idOrdenVenta}</h3>
                                <p>{venta.cliente || "Cliente no registrado"}</p>
                                <small>
                                    Canal: {venta.canalPedido} · Empleado: {venta.empleado}
                                </small>
                            </div>

                            <div className="venta-item-precio">
                                S/ {total.toFixed(2)}
                            </div>

                            <div className="venta-item-estado">
                                <span className={`status-badge ${venta.estado?.toLowerCase()}`}>
                                    {venta.estado}
                                </span>
                            </div>

                            <button
                                onClick={() => seleccionarOrden(esActivo ? null : venta)}
                                className={esActivo ? "btn-validado" : "btn-validar"}
                            >
                                {esActivo ? "Seleccionado" : "Seleccionar"}
                            </button>
                        </div>
                    );
                })}
            </div>
        </section>
    );
};

export default ValidarVenta;