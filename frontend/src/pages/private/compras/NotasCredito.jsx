import PanelProcesoCompra from "../../../components/private/compras/PanelProcesoCompra";
import { obtenerNotasCreditoProveedor, registrarNotaCreditoProveedor } from "../../../services/compras/comprasService";

const configuracion = {
    tipo: "nota",
    titulo: "Notas de crédito",
    descripcion: "Gestiona devoluciones, descuentos y diferencias de facturación.",
    accion: "Registrar nota de crédito",
    tituloTabla: "Notas de crédito registradas",
    listar: obtenerNotasCreditoProveedor,
    crear: registrarNotaCreditoProveedor,
    columnas: [
        { key: "numero", label: "Número", render: (item) => item.numero || item.codigo || item.idNotaCreditoProveedor },
        { key: "factura", label: "Factura", render: (item) => item.factura?.numero || item.idFacturaProveedor },
        { key: "motivo", label: "Motivo" },
        { key: "descripcion", label: "Descripción" },
        { key: "fechaEmision", label: "Fecha" },
        { key: "monto", label: "Monto", render: (item) => item.monto != null ? `S/ ${Number(item.monto).toFixed(2)}` : "-" },
    ],
};

export default function NotasCredito() {
    return <PanelProcesoCompra configuracion={configuracion} />;
}
