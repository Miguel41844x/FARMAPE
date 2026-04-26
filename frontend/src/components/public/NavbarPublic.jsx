import { Link } from "react-router-dom";
import { FaRegCircleUser } from "react-icons/fa6";
import "./navbarPublic.css"

const NavbarPublic = () => {
    return (
        <header className="header">
            <Link to="/" className="logo">
                <img src="/logo.jpeg" alt="logo" className="logo-img" />
                <div className="logo-text">
                    <span className="logo-top">Farmacias</span>
                    <span className="logo-bottom">Perú</span>
                </div>
            </Link>

            <div className="header-buttons">
                <Link to="/login" className="header-button-login">
                    Iniciar Sesión
                </Link>
            </div>
        </header>
    );
};

export default NavbarPublic;