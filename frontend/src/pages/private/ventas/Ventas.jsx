import { useEffect, useState } from "react";
import "./ventas.css";

import CarritoVenta from "../../../components/private/ventas/CarritoVenta";
import ProductosVenta from "../../../components/private/ventas/ProductosVenta";
import DatosVenta from "../../../components/private/ventas/DatosVenta";

import { obtenerProductos } from "../../../services/ventas/productoService";
import { obtenerClientes } from "../../../services/ventas/clienteService";
import { confirmarVenta, registrarVenta } from "../../../services/ventas/ventaService";

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
        cargarDatosIniciales();
    }, []);

    async function cargarDatosIniciales() {
        try {
            const productosData = await obtenerProductos();
            const clientesData = await obtenerClientes();

            setProductos(productosData);
            setClientes(clientesData);
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    }

    const cargarClientes = async () => {
        try {
            const clientesData = await obtenerClientes();
            setClientes(clientesData);
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
        const existe = carrito.find(
            (item) => item.idProducto === producto.idProducto
        );

        if (existe) {
            setCarrito(
                carrito.map((item) =>
                    item.idProducto === producto.idProducto
                        ? { ...item, cantidad: item.cantidad + 1 }
                        : item
                )
            );
        } else {
            setCarrito([
                ...carrito,
                {
                    idProducto: producto.idProducto,
                    nombre: producto.nombre,
                    categoria: producto.categoria,
                    precioVenta: Number(producto.precioVenta || 0),
                    stockActual: producto.stockActual,
                    cantidad: 1,
                },
            ]);
        }
    };

    const aumentarCantidad = (idProducto) => {
        setCarrito(
            carrito.map((item) =>
                item.idProducto === idProducto
                    ? { ...item, cantidad: item.cantidad + 1 }
                    : item
            )
        );
    };

    const disminuirCantidad = (idProducto) => {
        setCarrito(
            carrito
                .map((item) =>
                    item.idProducto === idProducto
                        ? { ...item, cantidad: item.cantidad - 1 }
                        : item
                )
                .filter((item) => item.cantidad > 0)
        );
    };

    const eliminarProducto = (idProducto) => {
        setCarrito(carrito.filter((item) => item.idProducto !== idProducto));
    };

    const totalVenta = carrito.reduce(
        (total, item) => total + Number(item.precioVenta || 0) * item.cantidad,
        0
    );

    const generarTicket = async () => {
        try {
            setLoadingTicket(true);

            if (!idCliente) {
                alert("Selecciona un cliente para registrar la venta");
                return;
            }

            if (carrito.length === 0) {
                alert("Agrega productos al carrito");
                return;
            }

            const hayProductoSinId = carrito.some((item) => !item.idProducto);

            if (hayProductoSinId) {
                console.error("Carrito con producto sin idProducto:", carrito);
                alert("Hay un producto sin ID válido en el carrito. Elimina el producto y vuelve a agregarlo.");
                return;
            }

            const ventaRequest = {
                idCliente: Number(idCliente),
                canalPedido,
                observacion: observacion || "Venta registrada desde frontend",
                detalles: carrito.map((item) => ({
                    idProducto: item.idProducto,
                    cantidad: item.cantidad,
                })),
            };

            const ventaCreada = await registrarVenta(ventaRequest);
            const ventaConfirmada = await confirmarVenta(ventaCreada.idOrdenVenta);

            alert(`Ticket generado y confirmado correctamente. Orden N° ${ventaConfirmada.idOrdenVenta}`);

            setCarrito([]);
            setIdCliente("");
            setCanalPedido("Presencial");
            setObservacion("");
            setPanelDerecho("productos");

            await cargarDatosIniciales();
        } catch (error) {
            console.error("Error:", error);
            alert("Error: " + error.message);
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
