import { useState } from "react";
import { NavLink } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";
import { CiShoppingCart, CiHome, CiFileOn } from "react-icons/ci";
import { FiLogOut } from "react-icons/fi";
import { PiListBold } from "react-icons/pi";
import { CiSettings } from "react-icons/ci";
import { CiCoinInsert } from "react-icons/ci";

import Logo from "./Logo";
import "./sidebar.css";

const Sidebar = () => {

    const navigate = useNavigate();
    const { user, logout } = useAuth();
    const [isOpen, setIsOpen] = useState(true);
    const [menuOpen, setMenuOpen] = useState(false);

    const handleLogout = () => {
        logout();
        navigate("/", {replace: true});
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
                <NavLink to="/homePrivate" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <CiHome />
                    {isOpen && <span>Inicio</span>}
                </NavLink>

                <NavLink to="/ventas" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <CiShoppingCart />
                    {isOpen && <span>Ventas</span>}
                </NavLink>

                <NavLink to="/caja" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <CiCoinInsert />
                    {isOpen && <span>Caja</span>}
                </NavLink>

                <NavLink to="/reportes" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <CiFileOn />
                    {isOpen && <span>Reportes</span>}
                </NavLink>

                <NavLink to="/mantenimiento" className={({ isActive }) => isActive ? "menu-item active" : "menu-item"}>
                    <CiSettings />
                    {isOpen && <span>Mantenimiento</span>}
                </NavLink>
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