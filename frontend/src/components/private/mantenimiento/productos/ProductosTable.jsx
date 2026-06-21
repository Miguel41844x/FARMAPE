import "./productosTable.css";

const ProductosTable = ({
    productos,
    totalProductos,
    busqueda,
    setBusqueda,
    loading,
    paginaActual,
    totalPaginas,
    paginaAnterior,
    paginaSiguiente,
    onEdit,
    onDelete,
}) => {
    return (
        <section className="productos-table-section">
            <div className="productos-table-header">
                <div>
                    <h2>Listado de productos</h2>
                    <span>{totalProductos} productos registrados</span>
                </div>

                <input
                    className="productos-search"
                    type="text"
                    placeholder="Buscar por nombre, laboratorio, categoría o estado"
                    value={busqueda}
                    onChange={(e) => setBusqueda(e.target.value)}
                />
            </div>

            <div className="productos-table-wrapper">
                <table className="productos-table">
                    <thead>
                        <tr>
                            <th>Producto</th>
                            <th>Categoría</th>
                            <th>Laboratorio</th>
                            <th>Precio compra</th>
                            <th>Precio venta</th>
                            <th>Stock</th>
                            <th>Stock mínimo</th>
                            <th>Vencimiento</th>
                            <th>Estado</th>
                            <th>Acciones</th>
                        </tr>
                    </thead>

                    <tbody>
                        {loading ? (
                            <tr>
                                <td colSpan="10" className="productos-empty">
                                    Cargando productos...
                                </td>
                            </tr>
                        ) : productos.length === 0 ? (
                            <tr>
                                <td colSpan="10" className="productos-empty">
                                    No se encontraron productos
                                </td>
                            </tr>
                        ) : (
                            productos.map((producto) => {
                                const categoria =
                                    typeof producto.categoria === "string"
                                        ? producto.categoria
                                        : producto.categoria?.nombre;

                                return (
                                <tr key={producto.idProducto}>
                                    <td>
                                        <strong>{producto.nombre}</strong>
                                        <small>{producto.descripcion || "Sin descripción"}</small>
                                    </td>

                                    <td>
                                        {categoria || "-"}
                                    </td>

                                    <td>
                                        {producto.laboratorio || "-"}
                                    </td>

                                    <td>
                                        S/ {Number(producto.precioCompra || 0).toFixed(2)}
                                    </td>

                                    <td>
                                        S/ {Number(producto.precioVenta || 0).toFixed(2)}
                                    </td>

                                    <td>
                                        {producto.stockActual ?? 0}
                                    </td>

                                    <td>
                                        {producto.stockMinimo ?? 0}
                                    </td>

                                    <td>
                                        {producto.fechaVencimiento
                                            ? new Date(producto.fechaVencimiento).toLocaleDateString("es-PE")
                                            : "-"}
                                    </td>

                                    <td>
                                        <span
                                            className={
                                                producto.estado === "ACTIVO"
                                                    ? "productos-status activo"
                                                    : "productos-status inactivo"
                                            }
                                        >
                                            {producto.estado}
                                        </span>
                                    </td>

                                    <td>
                                        <div className="productos-actions">
                                            <button onClick={() => onEdit(producto)}>
                                                Editar
                                            </button>

                                            <button
                                                className="delete"
                                                onClick={() => onDelete(producto.idProducto)}
                                            >
                                                Desactivar
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                                );
                            })
                        )}
                    </tbody>
                </table>
            </div>

            <div className="productos-pagination">
                <button
                    onClick={paginaAnterior}
                    disabled={paginaActual === 1}
                >
                    ‹
                </button>

                <span>
                    Página {paginaActual} de {totalPaginas || 1}
                </span>

                <button
                    onClick={paginaSiguiente}
                    disabled={paginaActual === totalPaginas || totalPaginas === 0}
                >
                    ›
                </button>
            </div>
        </section>
    );
};

export default ProductosTable;
