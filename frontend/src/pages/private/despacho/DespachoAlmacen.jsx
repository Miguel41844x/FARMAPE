import { FaTruck, FaBoxes, FaClipboardCheck, FaFileAlt } from "react-icons/fa";
import { MdStore } from "react-icons/md";
import { NavLink } from "react-router-dom";
import "./DespachoAlmacen.css";

function DespachoAlmacen() {
    const opciones = [
        {
            id: 1,
            titulo: "Entregar productos en tienda",
            descripcion: "Gestionar la entrega de pedidos pagados.",
            icono: <MdStore />,
            path: "/despacho-almacen/entrega",
        },
        {
            id: 2,
            titulo: "Reparto a domicilio",
            descripcion: "Administrar envíos y entregas.",
            icono: <FaTruck />,
            path: "/despacho-almacen/reparto",
        },
        {
            id: 3,
            titulo: "Registrar ingreso de productos",
            descripcion: "Ingresar productos al almacén.",
            icono: <FaBoxes />,
            path: "/despacho-almacen/ingreso",
        },
        {
            id: 4,
            titulo: "Verificar productos recibidos",
            descripcion: "Comparar pedidos con productos entregados.",
            icono: <FaClipboardCheck />,
            path: "/despacho-almacen/verificacion",
        },
        {
            id: 5,
            titulo: "Generar informe de almacén",
            descripcion: "Consultar reportes y movimientos.",
            icono: <FaFileAlt />,
            path: "/despacho-almacen/informes",
        },
    ];

    return (
        <div className="despacho-page">
            <h1>Despacho y Almacén</h1>

            <div className="cards-container">
                {opciones.map((opcion) => (
                    <NavLink
                        key={opcion.id}
                        to={opcion.path}
                        className="menu-card-link"
                    >
                        <div className="menu-card">
                            <div className="menu-icon">{opcion.icono}</div>
                            <h3>{opcion.titulo}</h3>
                            <p>{opcion.descripcion}</p>
                            <span className="menu-card-button">Ingresar</span>
                        </div>
                    </NavLink>
                ))}
            </div>
        </div>
    );
}

export default DespachoAlmacen;