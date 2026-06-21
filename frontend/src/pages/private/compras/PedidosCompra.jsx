import PanelProcesoCompra from "../../../components/private/compras/PanelProcesoCompra";
import { crearOrdenCompra, obtenerOrdenesCompra } from "../../../services/compras/comprasService";

const configuracion = {
    tipo: "pedido",
    titulo: "Pedidos a proveedores",
    descripcion: "Registra y consulta órdenes de abastecimiento.",
    accion: "Crear pedido",
    tituloTabla: "Pedidos registrados",
    listar: obtenerOrdenesCompra,
    crear: crearOrdenCompra,
    columnas: [
        { key: "codigo", label: "Código", render: (item) => item.codigo || item.numeroOrden || item.idOrdenCompra },
        { key: "proveedor", label: "Proveedor", render: (item) => item.proveedor?.razonSocial || item.razonSocialProveedor },
        { key: "fechaPedido", label: "Fecha", render: (item) => item.fechaPedido || item.fechaCreacion },
        { key: "fechaEntrega", label: "Entrega" },
        { key: "estado", label: "Estado" },
        { key: "total", label: "Total", render: (item) => item.total != null ? `S/ ${Number(item.total).toFixed(2)}` : "-" },
    ],
};

export default function PedidosCompra() {
    return <PanelProcesoCompra configuracion={configuracion} />;
}
