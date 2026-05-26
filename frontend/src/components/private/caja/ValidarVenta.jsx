import "./validarVenta.css";

const ValidarVenta = ({ ordenes = [], ordenSeleccionada, seleccionarOrden }) => {
    return (
        <section className="validar-card">

            <div className="caja-section-header">
                <h2>Validar orden de venta</h2>
                <span>{ordenes.length} órdenes</span>
            </div>

            <div className="ventas-list">

                {ordenes.map((venta) => {
                    const esActivo = ordenSeleccionada?.id === venta.id;

                    return (
                        <div 
                            className={`venta-item ${esActivo ? "activo" : ""}`} 
                            key={venta.id}
                        >
                            <div className="venta-item-info">
                                <h3>{venta.codigoVenta || venta.id}</h3>
                                <p>{venta.cliente}</p>
                            </div>

                            <div className="venta-item-precio">
                                {typeof venta.total === 'number' ? `S/ ${venta.total.toFixed(2)}` : venta.total}
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
                                {esActivo ? "Seleccionado" : "Validar"}
                            </button>

                        </div>
                    );
                })}

            </div>

        </section>
    );
};

export default ValidarVenta;