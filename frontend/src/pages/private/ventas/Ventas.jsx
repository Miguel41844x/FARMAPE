import { useEffect, useState } from "react";
import "./ventas.css";

import CarritoVenta from "../../../components/private/ventas/CarritoVenta";
import ProductosVenta from "../../../components/private/ventas/ProductosVenta";
import DatosVenta from "../../../components/private/ventas/DatosVenta";

const Ventas = () => {
    const [productos, setProductos] = useState([]);
    const [clientes, setClientes] = useState([]);

    const [busqueda, setBusqueda] = useState("");
    const [carrito, setCarrito] = useState([]);

    const [idCliente, setIdCliente] = useState("");
    const [canalPedido, setCanalPedido] = useState("Presencial");
    const [observacion, setObservacion] = useState("");

    const [panelDerecho, setPanelDerecho] = useState("productos");
    const [loadingTicket, setLoadingTicket] = useState(false);

    useEffect(() => {
        obtenerProductos();
        cargarClientes();
    }, []);

    const obtenerProductos = async () => {
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

    const cargarClientes = async () => {
        try {
            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/clientes", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al obtener clientes");
            }

            const data = await response.json();
            setClientes(data);
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    const productosFiltrados = productos.filter((producto) => {
        const texto = busqueda.toLowerCase();
        const nombre = producto.nombre?.toLowerCase() || "";
        const categoria = producto.categoria?.toLowerCase() || "";

        return nombre.includes(texto) || categoria.includes(texto);
    });

    const agregarProducto = (producto) => {
        const productoId = producto.idProducto || producto.id;
        const precioProducto = producto.precioVenta ?? producto.precio ?? 0;

        const existe = carrito.find((item) => item.id === productoId);

        if (existe) {
            setCarrito(
                carrito.map((item) =>
                    item.id === productoId
                        ? { ...item, cantidad: item.cantidad + 1 }
                        : item
                )
            );
        } else {
            setCarrito([
                ...carrito,
                {
                    id: productoId,
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

    const descargarPdf = (blob, nombreArchivo) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement("a");

        link.href = url;
        link.download = nombreArchivo;

        document.body.appendChild(link);
        link.click();

        link.remove();
        window.URL.revokeObjectURL(url);
    };

    const generarTicket = async () => {
        if (carrito.length === 0) {
            alert("Agrega productos a la orden");
            return;
        }

        if (!idCliente) {
            alert("Selecciona un cliente");
            return;
        }

        try {
            setLoadingTicket(true);

            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/ordenes/ticket", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    idCliente,
                    canalPedido,
                    observacion,
                    productos: carrito.map((item) => ({
                        productoId: item.id,
                        cantidad: item.cantidad,
                    })),
                }),
            });

            if (!response.ok) {
                throw new Error("No se pudo generar el ticket");
            }

            const blob = await response.blob();

            descargarPdf(blob, "ticket-orden.pdf");

            setCarrito([]);
            setIdCliente("");
            setCanalPedido("Presencial");
            setObservacion("");
            setPanelDerecho("productos");

            obtenerProductos();
        } catch (error) {
            console.error(error);
            alert(error.message);
        } finally {
            setLoadingTicket(false);
        }
    };

    return (
        <div className="ventas-container">
            <div className="ventas-header">
                <h1>Ventas</h1>
                <p>Registra productos y genera un ticket de orden para caja.</p>
            </div>

            <div className="ventas-layout">
                <CarritoVenta
                    carrito={carrito}
                    totalVenta={totalVenta}
                    aumentarCantidad={aumentarCantidad}
                    disminuirCantidad={disminuirCantidad}
                    eliminarProducto={eliminarProducto}
                    abrirDatosVenta={() => setPanelDerecho("datosVenta")}
                    loadingTicket={loadingTicket}
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

                    {panelDerecho === "datosVenta" && (
                        <DatosVenta
                            clientes={clientes}
                            idCliente={idCliente}
                            setIdCliente={setIdCliente}
                            canalPedido={canalPedido}
                            setCanalPedido={setCanalPedido}
                            observacion={observacion}
                            setObservacion={setObservacion}
                            cargarClientes={cargarClientes}
                            volverProductos={() => setPanelDerecho("productos")}
                            generarTicket={generarTicket}
                            loadingTicket={loadingTicket}
                            totalVenta={totalVenta}
                        />
                    )}
                </aside>
            </div>
        </div>
    );
};

export default Ventas;