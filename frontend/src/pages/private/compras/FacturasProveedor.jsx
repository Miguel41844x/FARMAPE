import PanelProcesoCompra from "../../../components/private/compras/PanelProcesoCompra";
import { obtenerFacturasProveedor, registrarFacturaProveedor } from "../../../services/compras/comprasService";

const configuracion = {
    tipo: "factura",
    titulo: "Facturas de proveedor",
    descripcion: "Registra comprobantes y relaciónalos con sus órdenes de compra.",
    accion: "Registrar factura",
    tituloTabla: "Facturas registradas",
    listar: obtenerFacturasProveedor,
    crear: registrarFacturaProveedor,
    columnas: [
        { key: "numero", label: "Comprobante", render: (item) => `${item.serie || ""}-${item.numero || ""}` },
        { key: "proveedor", label: "Proveedor", render: (item) => item.proveedor?.razonSocial || item.razonSocialProveedor },
        { key: "fechaEmision", label: "Emisión" },
        { key: "fechaVencimiento", label: "Vencimiento" },
        { key: "condicionPago", label: "Condición" },
        { key: "total", label: "Total", render: (item) => item.total != null ? `S/ ${Number(item.total).toFixed(2)}` : "-" },
    ],
};

export default function FacturasProveedor() {
    return <PanelProcesoCompra configuracion={configuracion} />;
}
