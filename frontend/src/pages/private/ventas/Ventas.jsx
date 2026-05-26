import { useEffect, useState } from "react";
import "./ventas.css";

import CarritoVenta from "../../../components/private/ventas/CarritoVenta";
import ProductosVenta from "../../../components/private/ventas/ProductosVenta";
import DatosVenta from "../../../components/private/ventas/DatosVenta";

const Ventas = () => {
    const [productos, setProductos] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [carrito, setCarrito] = useState([]);
    const [loadingTicket, setLoadingTicket] = useState(false);
    const [clientes, setClientes] = useState([]);
    const [idCliente, setIdCliente] = useState("");
    const [canalPedido, setCanalPedido] = useState("Presencial");
    const [observacion, setObservacion] = useState("");


    useEffect(() => {
        cargarClientes();
    }, []);

    const cargarClientes = async () => {
        try {
        const token = localStorage.getItem("token");

        const response = await fetch("http://localhost:8080/api/clientes", {
            method: "GET",
            headers: {
            Authorization: `Bearer ${token}`,
            },
        });

        if (!response.ok) {
            throw new Error("No se pudieron cargar los clientes");
        }

        const data = await response.json();
        setClientes(data);
        } catch (error) {
        console.error("Error al cargar clientes:", error);
        alert("No se pudieron cargar los clientes");
        }
    };

    useEffect(() => {
        obtenerProductos();
    }, []);

    const obtenerProductos = async () => {
        try {
            const token = localStorage.getItem("token");

            /* ENDPOINT A CAMBIAR */
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
                    item.id === producto.idProducto
                        ? { ...item, cantidad: item.cantidad + 1 }
                        : item
                )
            );
        } else {
            setCarrito([
                ...carrito,
                {
                    id: producto.idProducto,
                    nombre: producto.nombre,
                    categoria: producto.categoria,
                    precio: precioProducto,
                    stock: producto.stockActual,
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
        try {
            const token = localStorage.getItem("token");
            const idTrabajador = localStorage.getItem("idTrabajador");

            if (!idCliente) {
            alert("Selecciona un cliente para registrar la venta");
            return;
            }

            if (!idTrabajador) {
            alert("No se encontró el trabajador logueado. Cierra sesión e inicia sesión nuevamente.");
            return;
            }

            if (carrito.length === 0) {
            alert("Agrega productos al carrito");
            return;
            }

            const ventaRequest = {
            idCliente: Number(idCliente),
            idEmpleado: Number(idTrabajador),
            canalPedido,
            observacion: observacion || "Venta registrada desde frontend",
            detalles: carrito.map((item) => ({
                idProducto: item.idProducto,
                cantidad: item.cantidad,
            })),
            };

            const response = await fetch("http://localhost:8080/api/ventas", {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(ventaRequest),
            });

            if (!response.ok) {
            const errorText = await response.text();
            throw new Error(errorText || "No se pudo registrar la venta");
            }

            const ventaCreada = await response.json();

            alert(`Venta registrada correctamente. Orden N° ${ventaCreada.idOrdenVenta}`);

            setCarrito([]);
            setIdCliente("");
            setCanalPedido("Presencial");
            setObservacion("");
        } catch (error) {
            console.error("Error al registrar venta:", error);
            alert("Error al registrar venta: " + error.message);
        }
    };

    return (
        <div className="ventas-page">
            <div className="ventas-header">
                <h1>Ventas</h1>
                <p>Registra productos y genera un ticket de orden para caja.</p>
            </div>

            <div className="ventas-layout-superior">
                <CarritoVenta
                    carrito={carrito}
                    setCarrito={setCarrito}
                    totalVenta={totalVenta}
                    generarTicket={generarTicket}
                    loadingTicket={loadingTicket}
                    idCliente={idCliente}
                />

                <ProductosVenta
                    productos={productos}
                    busqueda={busqueda}
                    setBusqueda={setBusqueda}
                    agregarProducto={agregarProducto}
                />
            </div>

            <DatosVenta
                clientes={clientes}
                idCliente={idCliente}
                setIdCliente={setIdCliente}
                canalPedido={canalPedido}
                setCanalPedido={setCanalPedido}
                observacion={observacion}
                setObservacion={setObservacion}
                cargarClientes={cargarClientes}
            />
        </div>
    );
};

export default Ventas;