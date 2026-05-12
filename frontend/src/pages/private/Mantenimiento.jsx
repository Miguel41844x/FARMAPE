import "./mantenimiento.css";
import MaintenanceCard from "../../components/private/MaintenanceCard";
import MaintenanceTable from "../../components/private/MaintenanceTable";

const maintenanceOptions = [
    {
        title: "Usuarios",
        description: "Gestiona usuarios del sistema",
        buttonText: "Administrar usuarios",
    },
    {
        title: "Productos",
        description: "Mantén actualizado el inventario",
        buttonText: "Administrar productos",
    },
    {
        title: "Categorías",
        description: "Organiza productos por categorías",
        buttonText: "Administrar categorías",
    },
    {
        title: "Proveedores",
        description: "Gestiona proveedores registrados",
        buttonText: "Administrar proveedores",
    },
];

const Mantenimiento = () => {
    return (
        <div className="maintenance-container">
        <div className="maintenance-header">
            <h1>Mantenimiento</h1>
            <p>Panel administrativo para gestionar datos principales del sistema.</p>
        </div>

        <div className="maintenance-cards">
            {maintenanceOptions.map((option) => (
            <MaintenanceCard
                key={option.title}
                title={option.title}
                description={option.description}
                buttonText={option.buttonText}
            />
            ))}
        </div>

        <MaintenanceTable />
        </div>
    );
};

export default Mantenimiento;