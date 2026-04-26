import { useNavigate } from "react-router-dom";
import Carousel from "./Carousel";
import "./hero.css"

const Hero = () => {
    const navigate = useNavigate();
    
    return(
        <section className="hero">
            <div className="hero-content">
                <h1>Sistema Integral de Gestión Farmacéutica</h1>
                <p>Centraliza ventas, inventario, proveedores y recetas magistrales en una sola plataforma eficiente.</p>
                <div className="hero-buttons">
                    <button className="primary" onClick={() => navigate("/login")}>
                        Acceder al sistema
                    </button>
                </div>
            </div>
            <div className="hero-carousel">
                <Carousel/>
            </div>
        </section>
    );
};

export default Hero;