import { useEffect, useState } from "react";
import "./ventas.css";

import CarritoVenta from "../../components/private/ventas/CarritoVenta";
import ProductosVenta from "../../components/private/ventas/ProductosVenta";
import ClienteVenta from "../../components/private/ventas/ClienteVenta";

const MODO_DEV = true;

const productosPrueba = [
    {
        id: 1,
        nombre: "Paracetamol 500mg",
        categoria: "Analgésico",
        precio: 6.5,
        stock: 40,
    },
    {
        id: 2,
        nombre: "Ibuprofeno 400mg",
        categoria: "Antiinflamatorio",
        precio: 8.9,
        stock: 25,
    },
    {
        id: 3,
        nombre: "Loratadina 10mg",
        categoria: "Antialérgico",
        precio: 5.2,
        stock: 60,
    },
    {
        id: 4,
        nombre: "Amoxicilina 500mg",
        categoria: "Antibiótico",
        precio: 18.5,
        stock: 15,
    },
];

const Ventas = () => {
    const [productos, setProductos] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [carrito, setCarrito] = useState([]);
    const [panelDerecho, setPanelDerecho] = useState("productos");

    useEffect(() => {
        obtenerProductos();
    }, []);

    const obtenerProductos = async () => {
        if (MODO_DEV) {
            setProductos(productosPrueba);
            return;
        }

        try {
            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/productos", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al obtener productos");
            }

            const data = await response.json();
            setProductos(data);
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    const productosFiltrados = productos.filter((producto) => {
        const nombre = producto.nombre?.toLowerCase() || "";
        const categoria = producto.categoria?.toLowerCase() || "";
        const textoBusqueda = busqueda.toLowerCase();

        return (
            nombre.includes(textoBusqueda) ||
            categoria.includes(textoBusqueda)
        );
    });

    const agregarProducto = (producto) => {
        const existe = carrito.find((item) => item.id === producto.id);
        const precioProducto = producto.precioVenta ?? producto.precio ?? 0;

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
                    id: producto.id,
                    nombre: producto.nombre,
                    categoria: producto.categoria,
                    precio: precioProducto,
                    stock: producto.stock,
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

    const confirmarVenta = async (datosVenta) => {
        if (carrito.length === 0) {
            alert("Agrega productos a la venta");
            return;
        }

        const venta = {
            tipoComprobante: datosVenta.tipoComprobante,
            cliente: datosVenta.cliente,
            productos: carrito.map((item) => ({
                productoId: item.id,
                nombre: item.nombre,
                cantidad: item.cantidad,
                precioUnitario: item.precio,
                subtotal: item.precio * item.cantidad,
            })),
            total: totalVenta,
        };

        if (MODO_DEV) {
            console.log("Venta simulada:", venta);
            alert("Venta registrada correctamente en modo prueba");

            setCarrito([]);
            setPanelDerecho("productos");
            return;
        }

        try {
            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/ventas", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(venta),
            });

            if (!response.ok) {
                throw new Error("No se pudo registrar la venta");
            }

            alert("Venta registrada correctamente");

            setCarrito([]);
            setPanelDerecho("productos");
            obtenerProductos();
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    return (
        <div className="ventas-container">
            <div className="ventas-header">
                <h1>Ventas</h1>
                <p>Selecciona productos, registra el cliente y confirma la venta.</p>
            </div>

            <div className="ventas-layout">
                <CarritoVenta
                    carrito={carrito}
                    totalVenta={totalVenta}
                    aumentarCantidad={aumentarCantidad}
                    disminuirCantidad={disminuirCantidad}
                    eliminarProducto={eliminarProducto}
                    abrirCliente={() => setPanelDerecho("cliente")}
                />

                <aside className="ventas-right-panel">
                    {panelDerecho === "productos" && (
                        <ProductosVenta
                            busqueda={busqueda}
                            setBusqueda={setBusqueda}
                            productos={productosFiltrados}
                            agregarProducto={agregarProducto}
                        />
                    )}

                    {panelDerecho === "cliente" && (
                        <ClienteVenta
                            totalVenta={totalVenta}
                            carrito={carrito}
                            volverProductos={() => setPanelDerecho("productos")}
                            confirmarVenta={confirmarVenta}
                        />
                    )}
                </aside>
            </div>
        </div>
    );
};

export default Ventas;