import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const PermissionRoute = ({ permissions = [], requireAll = false }) => {
    const { user, hasPermission } = useAuth();

    if (!user) {
        return <Navigate to="/login" replace />;
    }

    const allowed = requireAll
        ? permissions.every(hasPermission)
        : permissions.some(hasPermission);

    return allowed ? <Outlet /> : <Navigate to="/homePrivate" replace />;
};

export default PermissionRoute;
