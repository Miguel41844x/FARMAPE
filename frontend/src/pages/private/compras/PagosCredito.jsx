import PanelProcesoCompra from "../../../components/private/compras/PanelProcesoCompra";
import { obtenerPagosProveedor, registrarPagoProveedor } from "../../../services/compras/comprasService";

const configuracion = {
    tipo: "pago",
    titulo: "Pagos a crédito",
    descripcion: "Registra pagos y controla los saldos pendientes de proveedores.",
    accion: "Registrar pago",
    tituloTabla: "Pagos registrados",
    listar: obtenerPagosProveedor,
    crear: registrarPagoProveedor,
    columnas: [
        { key: "idPagoProveedor", label: "Código", render: (item) => item.codigo || item.idPagoProveedor },
        { key: "factura", label: "Factura", render: (item) => item.factura?.numero || item.idFacturaProveedor },
        { key: "fechaPago", label: "Fecha" },
        { key: "metodoPago", label: "Método" },
        { key: "referencia", label: "Referencia" },
        { key: "monto", label: "Monto", render: (item) => item.monto != null ? `S/ ${Number(item.monto).toFixed(2)}` : "-" },
    ],
};

export default function PagosCredito() {
    return <PanelProcesoCompra configuracion={configuracion} />;
}
