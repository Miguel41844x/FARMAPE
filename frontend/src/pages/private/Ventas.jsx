import { useState, useEffect } from "react";
import "./ventas.css";

const Ventas = () => {
    const [productos, setProductos] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [carrito, setCarrito] = useState([]);

    useEffect(() => {
        obtenerProductos();
    }, []);

    const obtenerProductos = async () => {
        try{
            const token = localStorage.getItem("token");

            const response = await fetch ("http://localhost:8080/api/productos", {
                headers: {
                    Authorization: `Bearer ${token}`
                },
            });

            if (!response.ok){
                throw new Error ("Error al obtener productos");
            }

            const data = await response.json();
        } catch (error){
            console.error(error);
            alert(error.message);
        }
    };

    const productosFiltrados = productosIniciales.filter((producto) =>
        producto.nombre.toLowerCase(). includes(busqueda.toLowerCase())
    );

    const agregarProducto = (producto) => {
        const existe = carrito.find((item) => item.id === producto.id);

        if (existe) {
            setCarrito(
                carrito.map((item) =>
                    item.id === producto.id
                        ? { ...item, cantidad: item.cantidad + 1 }
                        : item
                )
            );
        } else {
            setCarrito([
                ...carrito,
                {
                    ...producto,
                    cantidad: 1,
                },
            ]);
        }
    };

    const aumentarCantidad = (id) => {
        setCarrito(
            carrito.map((item) =>
                item.id === id
                    ? { ...item, cantidad: item.cantidad + 1 }
                    : item
            )
        );
    };

    const disminuirCantidad = (id) => {
        setCarrito(
            carrito
                .map((item) =>
                    item.id === id
                        ? { ...item, cantidad: item.cantidad - 1 }
                        : item
                )
                .filter((item) => item.cantidad > 0)
        );
    };

    const eliminarProducto = (id) => {
        setCarrito(carrito.filter((item) => item.id !== id));
    };

    const totalVenta = carrito.reduce(
        (total, item) => total + item.precio * item.cantidad,
        0
    );

    const registrarVenta = () => {
        if (carrito.length === 0) {
            alert("Agrega productos a la venta");
            return;
        }

        console.log("Venta registrada:", carrito);
        alert("Venta registrada correctamente");
        setCarrito([]);
    };

    return (
        <div className="ventas-container">
            <div className="ventas-header">
                <h1>Ventas</h1>
                <p>Selecciona productos y registra una nueva venta.</p>
            </div>

            <div className="ventas-layout">
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

                        <button onClick={registrarVenta}>
                            Registrar venta
                        </button>
                    </div>
                </section>

                <aside className="venta-productos">
                    <div className="venta-section-header">
                        <h2>Productos disponibles</h2>
                        <span>{productosFiltrados.length} resultados</span>
                    </div>

                    <input
                        className="venta-search"
                        type="text"
                        placeholder="Buscar producto o categoría..."
                        value={busqueda}
                        onChange={(e) => setBusqueda(e.target.value)}
                    />

                    <div className="venta-productos-list">
                        {productosFiltrados.map((producto) => (
                            <div
                                className="venta-producto-card"
                                key={producto.id}
                            >
                                <div>
                                    <h3>{producto.nombre}</h3>
                                    <p>{producto.categoria}</p>
                                    <span>Stock: {producto.stock}</span>
                                </div>

                                <div className="venta-producto-action">
                                    <strong>S/ {producto.precio.toFixed(2)}</strong>
                                    <button onClick={() => agregarProducto(producto)}>
                                        Agregar
                                    </button>
                                </div>
                            </div>
                        ))}
                    </div>
                </aside>
            </div>
        </div>
    );
};

export default Ventas;