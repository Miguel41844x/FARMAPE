import "./mantenimiento.css";
import MaintenanceCard from "../../../components/private/mantenimiento/MaintenanceCard";
import { useAuth } from "../../../context/AuthContext";
import { PERMISSIONS } from "../../../constants/permissions";

const maintenanceOptions = [
    {
        title: "Usuarios",
        description: "Gestiona usuarios del sistema",
        buttonText: "Administrar usuarios",
        route: "/mantenimiento/usuarios",
        permission: PERMISSIONS.USER_MANAGE,
    },
    {
        title: "Roles y permisos",
        description: "Configura el acceso a cada módulo",
        buttonText: "Administrar roles",
        route: "/mantenimiento/roles",
        permission: PERMISSIONS.ROLE_MANAGE,
    },
];

const Mantenimiento = () => {
    const { hasPermission } = useAuth();
    const opcionesVisibles = maintenanceOptions.filter((option) => hasPermission(option.permission));

    return (
        <div className="maintenance-container">
            <div className="maintenance-header">
                <h1>Administración de usuarios</h1>
                <p>Gestiona cuentas, trabajadores y asignación de roles.</p>
            </div>

            <div className="maintenance-cards">
                {opcionesVisibles.map((option) => (
                <MaintenanceCard
                    key={option.title}
                    title={option.title}
                    description={option.description}
                    buttonText={option.buttonText}
                    route={option.route}
                />
                ))}
            </div>
        </div>
    );
};

export default Mantenimiento;
