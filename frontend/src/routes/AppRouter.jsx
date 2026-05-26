import { Route, Routes, Navigate } from "react-router-dom";
import PrivateLayout from "../pages/private/PrivateLayout";
import PrivateRoute from "./PrivateRoute";

// Páginas públicas
import HomePublic from "../pages/public/HomePublic";
import Login from "../pages/auth/Login";

// Páginas privadas
import HomePrivate from "../pages/private/HomePrivate";
import Ventas from "../pages/private/ventas/Ventas";
import Caja from "../pages/private/ventas/Caja";
import Reporte from "../pages/private/Reportes";
import Mantenimiento from "../pages/private/mantenimiento/Mantenimiento";
import Usuarios from "../pages/private/mantenimiento/Usuarios";

/**
 * AppRouter
 *
 * Maneja todas las rutas principales de la aplicación.
 *
 * Estructura:
 * - Rutas públicas
 * - Rutas privadas dentro de PrivateLayout
 */

function AppRouter (){
    return(
        <Routes>

            {/* Públicas */}
            <Route path="/" element={<HomePublic/>}/>
            <Route path="/login" element={<Login />} />

            {/* Privadas */}
            <Route element={<PrivateRoute/>} >
                <Route element={<PrivateLayout />} >
                    <Route path="/homePrivate" element={<HomePrivate/>} />
                    <Route path="/ventas" element={<Ventas/>}/>
                    <Route path="/caja" element={<Caja/>}/>
                    <Route path="/reportes" element={<Reporte/>}/>
                    <Route path="/mantenimiento" element={<Mantenimiento/>} />
                    <Route path="/mantenimiento/usuarios" element={<Usuarios/>}/>
                </Route>
            </Route>
                
        </Routes>
    );
}

export default AppRouter;