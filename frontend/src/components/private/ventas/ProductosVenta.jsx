import "./productosVenta.css";

const ProductosVenta = ({
    busqueda,
    setBusqueda,
    productos,
    agregarProducto,
}) => {
    return (
        <div className="venta-productos">
            <div className="venta-section-header">
                <h2>Productos disponibles</h2>
                <span>{productos.length} resultados</span>
            </div>

            <input
                className="venta-search"
                type="text"
                placeholder="Buscar medicamento o categoría..."
                value={busqueda}
                onChange={(e) => setBusqueda(e.target.value)}
            />

            <div className="venta-productos-list">
                {productos.length === 0 ? (
                    <div className="venta-productos-empty">
                        No se encontraron productos
                    </div>
                ) : (
                    productos.map((producto) => {
                        const precio = producto.precioVenta ?? producto.precio ?? 0;

                        return (
                            <div
                                className="venta-producto-card"
                                key={producto.id}
                            >
                                <div>
                                    <h3>{producto.nombre}</h3>
                                    <p>{producto.categoria || "Sin categoría"}</p>
                                    <span>Stock: {producto.stock}</span>
                                </div>

                                <div className="venta-producto-action">
                                    <strong>S/ {precio.toFixed(2)}</strong>

                                    <button onClick={() => agregarProducto(producto)}>
                                        Agregar
                                    </button>
                                </div>
                            </div>
                        );
                    })
                )}
            </div>
        </div>
    );
};

export default ProductosVenta;