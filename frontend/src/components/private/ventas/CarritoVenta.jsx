import "./carritoVenta.css";

const CarritoVenta = ({
    carrito,
    totalVenta,
    aumentarCantidad,
    disminuirCantidad,
    eliminarProducto,
    abrirDatosVenta,
    loadingTicket,
}) => {
    return (
        <section className="venta-carrito">
            <div className="venta-section-header">
                <h2>Productos a vender</h2>
                <span>{carrito.length} productos</span>
            </div>

            {carrito.length === 0 ? (
                <div className="venta-empty">
                    No hay productos agregados
                </div>
            ) : (
                <div className="venta-carrito-list">
                    {carrito.map((item) => (
                        <div className="venta-carrito-item" key={item.idProducto}>
                            <div>
                                <h3>{item.nombre}</h3>
                                <p>S/ {item.precioVenta.toFixed(2)}</p>
                            </div>

                            <div className="venta-item-actions">
                                <button onClick={() => disminuirCantidad(item.idProducto)}>
                                    -
                                </button>

                                <span>{item.cantidad}</span>

                                <button onClick={() => aumentarCantidad(item.idProducto)}>
                                    +
                                </button>
                            </div>

                            <strong>
                                S/ {(item.precioVenta * item.cantidad).toFixed(2)}
                            </strong>

                            <button
                                className="venta-remove-btn"
                                onClick={() => eliminarProducto(item.idProducto)}
                            >
                                Quitar
                            </button>
                        </div>
                    ))}
                </div>
            )}

            <div className="venta-total-card">
                <div>
                    <span>Total</span>
                    <h2>S/ {totalVenta.toFixed(2)}</h2>
                </div>

                <button
                    onClick={abrirDatosVenta}
                    disabled={carrito.length === 0 || loadingTicket}
                >
                    Generar ticket
                </button>
            </div>
        </section>
    );
};

export default CarritoVenta;