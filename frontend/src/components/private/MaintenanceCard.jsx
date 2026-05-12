import "./maintenanceCard.css";

const MaintenanceCard = ({ title, description, buttonText }) => {
    return (
        <div className="maintenance-card">
        <div>
            <h3>{title}</h3>
            <p>{description}</p>
        </div>

        <button>{buttonText}</button>
        </div>
    );
};

export default MaintenanceCard;