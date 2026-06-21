import { useState } from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

import { CiShoppingCart, CiHome, CiFileOn } from "react-icons/ci";
import { FiLogOut } from "react-icons/fi";
import { PiListBold } from "react-icons/pi";
import { CiSettings } from "react-icons/ci";
import { CiCoinInsert } from "react-icons/ci";
import { PiStorefrontLight } from "react-icons/pi";

import Logo from "./Logo";
import "./sidebar.css";

import { ROLES } from "../../constants/roles";

const menuItems = [
    {
        label: "Inicio",
        path: "/homePrivate",
        icon: <CiHome />,
        roles: [
            ROLES.EMPLEADO,
            ROLES.CAJERO,
            ROLES.ENCARGADO_DESPACHO,
            ROLES.ENCARGADO_ALMACEN,
            ROLES.ADMINISTRADOR,
            ROLES.QUIMICO_FARMACEUTICO,
            ROLES.GERENTE,
            ROLES.ADMINISTRADOR,
        ],
    },
    {
        label: "Ventas",
        path: "/ventas",
        icon: <CiShoppingCart />,
        roles: [
            ROLES.EMPLEADO,
            ROLES.ADMINISTRADOR,
        ],
    },
    {
        label: "Caja",
        path: "/caja",
        icon: <CiCoinInsert />,
        roles: [
            ROLES.CAJERO,
            ROLES.ADMINISTRADOR,
        ],
    },
    {
        label: "Mantenimiento",
        path: "/mantenimiento",
        icon: <CiSettings />,
        roles: [
            ROLES.ADMINISTRADOR,
        ],
    },
    {
        label: "Despacho y almacén",
        path: "/despacho-almacen",
        icon: <PiStorefrontLight />,
        roles: [
            ROLES.ADMINISTRADOR,
        ],
    },
    {
        label: "Reportes",
        path: "/reportes",
        icon: <CiFileOn />,
        roles: [
            ROLES.GERENTE,
            ROLES.ADMINISTRADOR
        ],
    },
];

const Sidebar = () => {

    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const [isOpen, setIsOpen] = useState(true);
    const [menuOpen, setMenuOpen] = useState(false);

    const rolUsuario = user?.rol;

    const visibleMenuItems = menuItems.filter((item) =>
        item.roles.includes(rolUsuario)
    );

    const handleLogout = () => {
        logout();
        navigate("/", { replace: true });
    };

    const initial = user?.nombres?.charAt(0).toUpperCase();

    return (
        <aside className={`sidebar ${isOpen ? "open" : "closed"}`}>

            <div className="sidebar-header">
                <PiListBold
                    className="menu-toggle"
                    onClick={() => setIsOpen(!isOpen)}
                />
                {isOpen && <Logo to="/homePrivate" />}
            </div>

            <nav className="sidebar-menu">
                {visibleMenuItems.map((item) => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        className={({ isActive }) =>
                            isActive ? "menu-item active" : "menu-item"
                        }
                    >
                        {item.icon}
                        {isOpen && <span>{item.label}</span>}
                    </NavLink>
                ))}
            </nav>

            {isOpen && (
                <div className="sidebar-footer">
                    <div
                        className="user-box"
                        onClick={() => setMenuOpen(!menuOpen)}
                    >
                        <div className="avatar">
                            {initial}
                        </div>

                        <div className="user-info">
                            <span className="user-name">{user?.nombres}</span>
                            <span className="user-rol">{user?.rol}</span>
                        </div>
                    </div>

                    {menuOpen && (
                        <div className="user-dropdown">
                            <button onClick={handleLogout}>
                                <FiLogOut />
                                Cerrar sesión
                            </button>
                        </div>
                    )}
                </div>
            )}

        </aside>
    );
};

export default Sidebar;