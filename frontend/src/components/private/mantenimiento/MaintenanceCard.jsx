import "./maintenanceCard.css";
import { NavLink } from "react-router-dom";

const MaintenanceCard = ({ title, description, buttonText, route }) => {
    return (
        <div className="maintenance-card">
            <div>
                <h3>{title}</h3>
                <p>{description}</p>
            </div>
            <NavLink to={route} className="maintenance-card-btn">
                <button>{buttonText}</button>
            </NavLink>
        </div>
    );
};

export default MaintenanceCard;