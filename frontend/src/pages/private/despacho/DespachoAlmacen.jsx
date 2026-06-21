import { FaTruck, FaBoxes, FaClipboardCheck, FaFileAlt } from "react-icons/fa";
import { MdStore } from "react-icons/md";
import { NavLink } from "react-router-dom";
import "./DespachoAlmacen.css";

function DespachoAlmacen() {
    const opciones = [
        {
            id: 1,
            codigo: "DES-01",
            titulo: "Entregar productos en tienda",
            descripcion: "Gestionar la entrega de pedidos pagados.",
            icono: <MdStore />,
            path: "/despacho-almacen/entrega",
        },
        {
            id: 2,
            codigo: "DES-02",
            titulo: "Reparto a domicilio",
            descripcion: "Administrar envíos y entregas.",
            icono: <FaTruck />,
            path: "/despacho-almacen/reparto",
        },
        {
            id: 3,
            codigo: "ALM-01",
            titulo: "Registrar ingreso de productos",
            descripcion: "Ingresar productos al almacén.",
            icono: <FaBoxes />,
            path: "/despacho-almacen/ingreso",
        },
        {
            id: 4,
            codigo: "ALM-02",
            titulo: "Verificar productos recibidos",
            descripcion: "Comparar pedidos con productos entregados.",
            icono: <FaClipboardCheck />,
            path: "/despacho-almacen/verificacion",
        },
        {
            id: 5,
            codigo: "ALM-03",
            titulo: "Generar informe de almacén",
            descripcion: "Consultar reportes y movimientos.",
            icono: <FaFileAlt />,
            path: "/despacho-almacen/informes",
        },
    ];

    return (
        <div className="despacho-page">
            <div className="despacho-header">
                <h1>Despacho y almacén</h1>
                <p>Selecciona una opción para gestionar entregas y movimientos de almacén.</p>
            </div>

            <div className="despacho-cards">
                {opciones.map((opcion) => (
                    <NavLink
                        key={opcion.id}
                        to={opcion.path}
                        className="despacho-card-link"
                    >
                        <article className="despacho-card">
                            <div className="despacho-card-icon">{opcion.icono}</div>
                            <span className="despacho-card-code">{opcion.codigo}</span>
                            <h2>{opcion.titulo}</h2>
                            <p>{opcion.descripcion}</p>
                            <span className="despacho-card-button">Ingresar</span>
                        </article>
                    </NavLink>
                ))}
            </div>
        </div>
    );
}

export default DespachoAlmacen;
