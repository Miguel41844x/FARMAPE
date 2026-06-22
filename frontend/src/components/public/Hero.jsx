import { useNavigate } from "react-router-dom";
import Carousel from "./Carousel";
import "./hero.css"

const Hero = () => {
    const navigate = useNavigate();
    
    return(
        <section className="hero">
            <div className="hero-content">
                <div className="hero-content-tittle">
                    <h4>Sistema de gestión farmacéutica</h4>
                </div>
                <h1>Gestión de procesos con <span>seguridad</span> y <span>eficiencia</span></h1>
                <p>En Farmacias Perú trabajamos para simplificar la gestión diaria, integrando control, eficiencia y confianza 
                    en cada proceso. Nuestra plataforma reúne ventas, inventario y clientes en un solo lugar,
                    permitiendo una operación más ágil, ordenada y segura.</p>
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
