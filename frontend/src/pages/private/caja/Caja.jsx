import { useEffect, useState } from "react";
import "./caja.css";

import ValidarVenta from "../../components/private/caja/ValidarVenta";
import PagoComprobante from "../../components/private/caja/PagoComprobante";
import EstadoVenta from "../../components/private/caja/EstadoVenta";

const MODO_DEV = true;

const ordenesPrueba = [
    {
        id: 1,
        codigoVenta: "V001",
        cliente: "Juan Pérez",
        tipoComprobante: "Boleta",
        estado: "Pendiente",
        total: 85.60,
        productos: [
            { productoId: 1, nombre: "Paracetamol 500mg", cantidad: 10, precioUnitario: 6.5, subtotal: 65.0 },
            { productoId: 3, nombre: "Loratadina 10mg", cantidad: 3, precioUnitario: 5.2, subtotal: 15.6 }
        ]
    },
    {
        id: 2,
        codigoVenta: "V002",
        cliente: "María López",
        tipoComprobante: "Factura",
        estado: "Pendiente",
        total: 35.60,
        productos: [
            { productoId: 2, nombre: "Ibuprofeno 400mg", cantidad: 4, precioUnitario: 8.9, subtotal: 35.6 }
        ]
    }
];

const Caja = () => {
    const [ordenes, setOrdenes] = useState([]);
    const [ordenSeleccionada, setOrdenSeleccionada] = useState(null);

    useEffect(() => {
        obtenerOrdenesPendientes();
    }, []);

    const obtenerOrdenesPendientes = async () => {
        if (MODO_DEV) {
            setOrdenes(prevOrdenes => prevOrdenes.length > 0 ? prevOrdenes : ordenesPrueba);
            return;
        }

        try {
            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/ventas/pendientes", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("Error al obtener las órdenes pendientes");
            }

            const data = await response.json();
            setOrdenes(data);
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    const procesarPago = async (datosPago) => {
        if (!ordenSeleccionada) {
            alert("Por favor, selecciona una orden de venta primero");
            return;
        }

        const pago = {
            ventaId: ordenSeleccionada.id,
            codigoVenta: ordenSeleccionada.codigoVenta,
            metodoPago: datosPago.metodoPago, // 'Efectivo', 'Tarjeta', 'Yape'
            montoRecibido: parseFloat(datosPago.montoRecibido) || 0,
            total: ordenSeleccionada.total,
            vuelto: (parseFloat(datosPago.montoRecibido) || 0) - ordenSeleccionada.total
        };

        if (MODO_DEV) {
            alert("Comprobante emitido y pago registrado correctamente (Modo Prueba)");
            
            const ordenesActualizadas = ordenes.map(item => 
                item.id === ordenSeleccionada.id 
                    ? { ...item, estado: "Pagado", metodoPago: datosPago.metodoPago, montoRecibido: datosPago.montoRecibido } 
                    : item
            );
            setOrdenes(ordenesActualizadas);
            setOrdenSeleccionada({ ...ordenSeleccionada, estado: "Pagado", metodoPago: datosPago.metodoPago, montoRecibido: datosPago.montoRecibido });
            return;
        }

        try {
            const token = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/caja/pagar", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify(pago),
            });

            if (!response.ok) {
                throw new Error("No se pudo registrar el pago de la orden");
            }

            alert("Pago procesado con éxito y venta finalizada");
            
            obtenerOrdenesPendientes(); 
            setOrdenSeleccionada({ ...ordenSeleccionada, estado: "Pagado", metodoPago: datosPago.metodoPago, montoRecibido: datosPago.montoRecibido });
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    const actualizarEstadoOrden = async (nuevoEstado) => {
        if (MODO_DEV) {
            alert(`Estado actualizado a ${nuevoEstado} con éxito`);
            
            const ordenesActualizadas = ordenes.map(item => 
                item.id === ordenSeleccionada.id 
                    ? { ...item, estado: nuevoEstado, metodoPago: ordenSeleccionada.metodoPago, montoRecibido: ordenSeleccionada.montoRecibido } 
                    : item
            );
            setOrdenes(ordenesActualizadas);
            setOrdenSeleccionada({ ...ordenSeleccionada, estado: nuevoEstado, metodoPago: ordenSeleccionada.metodoPago, montoRecibido: ordenSeleccionada.montoRecibido });
            return;
        }

        try {
            const token = localStorage.getItem("token");
            const response = await fetch(`http://localhost:8080/api/caja/estado`, {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({ ventaId: ordenSeleccionada.id, estado: nuevoEstado }),
            });

            if (!response.ok) throw new Error("No se pudo actualizar el estado");

            alert("Estado actualizado correctamente");
            obtenerOrdenesPendientes();
            
            setOrdenSeleccionada({ ...ordenSeleccionada, estado: nuevoEstado, metodoPago: ordenSeleccionada.metodoPago, montoRecibido: ordenSeleccionada.montoRecibido });
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    return (
        <div className="caja-container">
            <div className="caja-header">
                <h1>Caja</h1>
                <p>Valida órdenes, registra pagos y actualiza el estado de venta.</p>
            </div>

            <div className="caja-layout">
                <ValidarVenta 
                    ordenes={ordenes}
                    ordenSeleccionada={ordenSeleccionada}
                    seleccionarOrden={setOrdenSeleccionada}
                />

                <aside className="caja-right-panel">
                    <PagoComprobante 
                        orden={ordenSeleccionada}
                        procesarPago={procesarPago}
                    />
                    
                    <EstadoVenta 
                        orden={ordenSeleccionada}
                        actualizarEstado={actualizarEstadoOrden}
                    />
                </aside>
            </div>
        </div>
    );
};

export default Caja;