import { useEffect, useState } from "react";
import "./caja.css";

import ValidarVenta from "../../../components/private/caja/ValidarVenta";
import PagoComprobante from "../../../components/private/caja/PagoComprobante";
import EstadoVenta from "../../../components/private/caja/EstadoVenta";

import { listarOrdenesPendientesCaja, obtenerOrdenCaja,
    registrarPagoCaja, } from "../../../services/caja/cajaService";

const Caja = () => {
    const [ordenes, setOrdenes] = useState([]);
    const [ordenSeleccionada, setOrdenSeleccionada] = useState(null);
    const [resultadoPago, setResultadoPago] = useState(null);
    const [loadingOrdenes, setLoadingOrdenes] = useState(false);
    const [loadingPago, setLoadingPago] = useState(false);

    useEffect(() => {
        obtenerOrdenesPendientes();
    }, []);

    async function obtenerOrdenesPendientes() {
        try {
            setLoadingOrdenes(true);

            const data = await listarOrdenesPendientesCaja();
            setOrdenes(data);
        } catch (error) {
            console.error("Error al cargar órdenes pendientes:", error);
            alert(error.message);
        } finally {
            setLoadingOrdenes(false);
        }
    }

    const seleccionarOrden = async (orden) => {
        try {
            if (!orden) {
                setOrdenSeleccionada(null);
                setResultadoPago(null);
                return;
            }

            const data = await obtenerOrdenCaja(orden.idOrdenVenta);

            setOrdenSeleccionada(data);
            setResultadoPago(null);
        } catch (error) {
            console.error("Error al seleccionar orden:", error);
            alert(error.message);
        }
    };

    const procesarPago = async (datosPago) => {
        if (!ordenSeleccionada) {
            alert("Selecciona una orden de venta primero");
            return;
        }

        try {
            setLoadingPago(true);

            const request = {
                montoPagado: Number(datosPago.montoPagado),
                metodoPago: datosPago.metodoPago,
                tipoComprobante: datosPago.tipoComprobante,
            };

            const data = await registrarPagoCaja(
                ordenSeleccionada.idOrdenVenta,
                request
            );

            setResultadoPago(data);
            setOrdenSeleccionada(data.ordenVenta);

            await obtenerOrdenesPendientes();

            alert(
                `Pago registrado correctamente. Comprobante: ${data.comprobante.serie}-${data.comprobante.numero}`
            );
        } catch (error) {
            console.error("Error al procesar pago:", error);
            alert("Error al procesar pago: " + error.message);
        } finally {
            setLoadingPago(false);
        }
    };

    return (
        <div className="caja-container">
            <div className="caja-header">
                <h1>Caja</h1>
                <p>Valida órdenes confirmadas, registra pagos y emite comprobantes.</p>
            </div>

            <div className="caja-layout">
                <ValidarVenta
                    ordenes={ordenes}
                    ordenSeleccionada={ordenSeleccionada}
                    seleccionarOrden={seleccionarOrden}
                    loading={loadingOrdenes}
                />

                <aside className="caja-right-panel">
                    <PagoComprobante
                        key={ordenSeleccionada?.idOrdenVenta || "sin-orden"}
                        orden={ordenSeleccionada}
                        procesarPago={procesarPago}
                        loadingPago={loadingPago}
                    />

                    <EstadoVenta
                        orden={ordenSeleccionada}
                        resultadoPago={resultadoPago}
                    />
                </aside>
            </div>
        </div>
    );
};

export default Caja;
