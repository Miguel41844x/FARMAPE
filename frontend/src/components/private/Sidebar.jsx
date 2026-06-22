import { useState } from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

import { CiShoppingCart, CiHome, CiFileOn, CiMedicalCase } from "react-icons/ci";
import { FiActivity, FiLogOut, FiUser } from "react-icons/fi";
import { PiListBold } from "react-icons/pi";
import { CiSettings } from "react-icons/ci";
import { CiCoinInsert } from "react-icons/ci";
import { PiStorefrontLight, PiTruckLight } from "react-icons/pi";

import Logo from "./Logo";
import UserProfileModal from "./perfil/UserProfileModal";
import "./sidebar.css";

import { PERMISSIONS } from "../../constants/permissions";

const menuItems = [
    {
        label: "Inicio",
        path: "/homePrivate",
        icon: <CiHome />,
        permissions: [PERMISSIONS.HOME_VIEW],
    },
    {
        label: "Ventas",
        path: "/ventas",
        icon: <CiShoppingCart />,
        permissions: [PERMISSIONS.SALE_CREATE],
    },
    {
        label: "Caja",
        path: "/caja",
        icon: <CiCoinInsert />,
        permissions: [PERMISSIONS.PAYMENT_READ],
    },
    {
        label: "Administración de usuarios",
        path: "/mantenimiento",
        icon: <CiSettings />,
        permissions: [PERMISSIONS.USER_MANAGE, PERMISSIONS.ROLE_MANAGE, PERMISSIONS.ROLE_ASSIGN],
    },
    {
        label: "Productos",
        path: "/productos",
        icon: <PiStorefrontLight />,
        permissions: [PERMISSIONS.PRODUCT_MANAGE],
    },
    {
        label: "Compras y proveedores",
        path: "/compras-proveedores",
        icon: <PiTruckLight />,
        permissions: [PERMISSIONS.PURCHASE_MANAGE],
    },
    {
        label: "Despacho y almacén",
        path: "/despacho-almacen",
        icon: <PiStorefrontLight />,
        permissions: [PERMISSIONS.DISPATCH_MANAGE, PERMISSIONS.INVENTORY_MANAGE],
    },
    {
        label: "Recetas Magistrales",
        path: "/recetas",
        icon: <CiMedicalCase />,
        permissions: [PERMISSIONS.FORMULA_MANAGE],
    },
    {
        label: "Reportes",
        path: "/reportes",
        icon: <CiFileOn />,
        permissions: [PERMISSIONS.REPORT_VIEW],
    },
    {
        label: "Auditoría",
        path: "/auditoria",
        icon: <FiActivity />,
        permissions: [PERMISSIONS.AUDIT_VIEW],
    },
];

const Sidebar = () => {
    const navigate = useNavigate();
    const { user, logout, hasPermission } = useAuth();
    const [isOpen, setIsOpen] = useState(true);
    const [menuOpen, setMenuOpen] = useState(false);
    const [profileOpen, setProfileOpen] = useState(false);

    const visibleMenuItems = menuItems.filter((item) =>
        item.permissions.some(hasPermission)
    );

    const handleLogout = () => {
        logout();
        navigate("/", { replace: true });
    };

    const handleOpenProfile = () => {
        setMenuOpen(false);
        setProfileOpen(true);
    };

    const initial = user?.nombres?.charAt(0).toUpperCase();

    return (
        <aside className={`sidebar ${isOpen ? "open" : "closed"}`}>
            <div className="sidebar-header">
                <button
                    type="button"
                    className="menu-toggle"
                    aria-label={isOpen ? "Contraer menú" : "Expandir menú"}
                    title={isOpen ? "Contraer menú" : "Expandir menú"}
                    onClick={() => setIsOpen(!isOpen)}
                >
                    <PiListBold />
                </button>
                {isOpen && <Logo to="/homePrivate" />}
            </div>

            <nav className="sidebar-menu">
                {isOpen && <span className="sidebar-section-label">Navegación</span>}
                {visibleMenuItems.map((item) => (
                    <NavLink
                        key={item.path}
                        to={item.path}
                        title={!isOpen ? item.label : undefined}
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
                        role="button"
                        tabIndex="0"
                        aria-expanded={menuOpen}
                        onClick={() => setMenuOpen(!menuOpen)}
                        onKeyDown={(event) => {
                            if (event.key === "Enter" || event.key === " ") setMenuOpen(!menuOpen);
                        }}
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
                            <button onClick={handleOpenProfile}>
                                <FiUser />
                                Mi perfil
                            </button>
                            <button className="logout-option" onClick={handleLogout}>
                                <FiLogOut />
                                Cerrar sesión
                            </button>
                        </div>
                    )}
                </div>
            )}

            <UserProfileModal
                open={profileOpen}
                onClose={() => setProfileOpen(false)}
            />
        </aside>
    );
};

export default Sidebar;
