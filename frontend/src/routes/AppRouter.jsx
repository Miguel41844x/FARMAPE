import { Route, Routes } from "react-router-dom";
import PrivateLayout from "../pages/private/PrivateLayout";
import PrivateRoute from "./PrivateRoute";
import RoleRoute from "./RoleRoute";

import { ROLES } from "../constants/roles";

// Páginas públicas
import HomePublic from "../pages/public/HomePublic";
import Login from "../pages/auth/Login";

// Páginas privadas
import HomePrivate from "../pages/private/HomePrivate";
import Ventas from "../pages/private/ventas/Ventas";
import Caja from "../pages/private/caja/Caja";
import Reporte from "../pages/private/Reportes";
import Mantenimiento from "../pages/private/mantenimiento/Mantenimiento";
import Usuarios from "../pages/private/mantenimiento/Usuarios";

function AppRouter() {
    return (
        <Routes>

            {/* Públicas */}
            <Route path="/" element={<HomePublic />} />
            <Route path="/login" element={<Login />} />

            {/* Privadas */}
            <Route element={<PrivateRoute />}>
                <Route element={<PrivateLayout />}>

                    <Route path="/homePrivate" element={<HomePrivate />} />

                    <Route
                        element={
                            <RoleRoute
                                allowedRoles={[
                                    ROLES.EMPLEADO,
                                    ROLES.ADMINISTRADOR,
                                ]}
                            />
                        }
                    >
                        <Route path="/ventas" element={<Ventas />} />
                    </Route>

                    <Route
                        element={
                            <RoleRoute
                                allowedRoles={[
                                    ROLES.CAJERO,
                                ]}
                            />
                        }
                    >
                        <Route path="/caja" element={<Caja />} />
                    </Route>

                    <Route
                        element={
                            <RoleRoute
                                allowedRoles={[
                                    ROLES.ADMINISTRADOR,
                                ]}
                            />
                        }
                    >
                        <Route path="/mantenimiento" element={<Mantenimiento />} />
                        <Route path="/mantenimiento/usuarios" element={<Usuarios />} />
                    </Route>

                    <Route
                        element={
                            <RoleRoute
                                allowedRoles={[
                                    ROLES.GERENTE,
                                ]}
                            />
                        }
                    >
                        <Route path="/reportes" element={<Reporte />} />
                    </Route>

                </Route>
            </Route>

        </Routes>
    );
}

export default AppRouter;