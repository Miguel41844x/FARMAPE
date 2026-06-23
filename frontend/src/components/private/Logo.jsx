import { Link } from "react-router-dom";
import "./logo.css"

const Logo = ({ to }) => {
    return (
        <Link to={to} className="logo">
        <img src="/logo.jpeg" alt="logo" className="logo-img"/>
        <div className="logo-text">
            <span className="logo-top">Farmaceuticas</span>
            <span className="logo-bottom">Perú</span>
        </div>
        </Link>
    );
};

export default Logo;