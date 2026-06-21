import { NavLink } from "react-router-dom";
import {
    FiCreditCard,
    FiFileText,
    FiPackage,
    FiRefreshCcw,
    FiTruck,
} from "react-icons/fi";
import "./comprasProveedores.css";

const opciones = [
    {
        titulo: "Realizar pedido a proveedor",
        descripcion: "Registrar y consultar órdenes de compra.",
        icono: <FiPackage />,
        path: "/compras-proveedores/pedidos",
    },
    {
        titulo: "Registrar factura de proveedor",
        descripcion: "Gestionar los comprobantes de las compras.",
        icono: <FiFileText />,
        path: "/compras-proveedores/facturas",
    },
    {
        titulo: "Gestionar notas de crédito",
        descripcion: "Consultar devoluciones y ajustes comerciales.",
        icono: <FiRefreshCcw />,
        path: "/compras-proveedores/notas-credito",
    },
    {
        titulo: "Controlar pagos a crédito",
        descripcion: "Revisar saldos y fechas de vencimiento.",
        icono: <FiCreditCard />,
        path: "/compras-proveedores/pagos",
    },
    {
        titulo: "Gestionar proveedores",
        descripcion: "Registrar y mantener los datos de proveedores.",
        icono: <FiTruck />,
        path: "/compras-proveedores/proveedores",
    },
];

function ComprasProveedores() {
    return (
        <main className="compras-page">
            <header className="compras-header">
                <h1>Compras y proveedores</h1>
                <p>Selecciona una opción para gestionar las compras de la farmacia.</p>
            </header>

            <section className="compras-cards">
                {opciones.map((opcion) => (
                    <NavLink key={opcion.path} to={opcion.path} className="compras-card-link">
                        <article className="compras-card">
                            <div className="compras-card-icon">{opcion.icono}</div>
                            <h2>{opcion.titulo}</h2>
                            <p>{opcion.descripcion}</p>
                            <span className="compras-card-button">Ingresar</span>
                        </article>
                    </NavLink>
                ))}
            </section>
        </main>
    );
}

export default ComprasProveedores;
