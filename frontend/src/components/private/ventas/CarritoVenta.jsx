import "./carritoVenta.css";

const CarritoVenta = ({
    carrito,
    totalVenta,
    aumentarCantidad,
    disminuirCantidad,
    eliminarProducto,
    abrirCliente,
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
                        <div className="venta-carrito-item" key={item.id}>
                            <div>
                                <h3>{item.nombre}</h3>
                                <p>S/ {item.precio.toFixed(2)}</p>
                            </div>

                            <div className="venta-item-actions">
                                <button onClick={() => disminuirCantidad(item.id)}>
                                    -
                                </button>

                                <span>{item.cantidad}</span>

                                <button onClick={() => aumentarCantidad(item.id)}>
                                    +
                                </button>
                            </div>

                            <strong>
                                S/ {(item.precio * item.cantidad).toFixed(2)}
                            </strong>

                            <button
                                className="venta-remove-btn"
                                onClick={() => eliminarProducto(item.id)}
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
                    onClick={abrirCliente}
                    disabled={carrito.length === 0}
                >
                    Datos del cliente
                </button>
            </div>
        </section>
    );
};

export default CarritoVenta;