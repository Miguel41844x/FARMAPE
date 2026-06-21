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
import Productos from "../pages/private/mantenimiento/Productos";
import DespachoAlmacen from "../pages/private/despacho/DespachoAlmacen";
import EntregaTienda from "../pages/private/despacho/EntregaTienda";
import RepartoDomicilio from "../pages/private/despacho/RepartoDomicilio";
import RegistrarIngreso from "../pages/private/despacho/RegistrarIngreso";
import VerificarProductos from "../pages/private/despacho/VerificarProductos";
import InformeAlmacen from "../pages/private/despacho/InformeAlmacen";
import ComprasProveedores from "../pages/private/compras/ComprasProveedores";
import PedidosCompra from "../pages/private/compras/PedidosCompra";
import FacturasProveedor from "../pages/private/compras/FacturasProveedor";
import NotasCredito from "../pages/private/compras/NotasCredito";
import PagosCredito from "../pages/private/compras/PagosCredito";
import ProveedoresCompras from "../pages/private/compras/ProveedoresCompras";

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
                                    ROLES.ADMINISTRADOR,
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
                        <Route path="/mantenimiento/productos" element={<Productos/>} />
                    </Route>

                    <Route
                        element={
                            <RoleRoute
                                allowedRoles={[
                                    ROLES.GERENTE,
                                    ROLES.ADMINISTRADOR,
                                ]}
                            />
                        }
                    >
                        <Route path="/reportes" element={<Reporte />} />
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
                        <Route path="/compras-proveedores" element={<ComprasProveedores />} />
                        <Route path="/compras-proveedores/pedidos" element={<PedidosCompra />} />
                        <Route path="/compras-proveedores/facturas" element={<FacturasProveedor />} />
                        <Route path="/compras-proveedores/notas-credito" element={<NotasCredito />} />
                        <Route path="/compras-proveedores/pagos" element={<PagosCredito />} />
                        <Route path="/compras-proveedores/proveedores" element={<ProveedoresCompras />} />
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
                        <Route path="/despacho-almacen" element={<DespachoAlmacen/>}/>
                        <Route path="/despacho-almacen/entrega" element={<EntregaTienda />} />
                        <Route path="/despacho-almacen/reparto" element={<RepartoDomicilio />} />
                        <Route path="/despacho-almacen/ingreso" element={<RegistrarIngreso />} />
                        <Route path="/despacho-almacen/verificacion" element={<VerificarProductos />} />
                        <Route path="/despacho-almacen/informes" element={<InformeAlmacen />} />
                    </Route>

                </Route>
            </Route>

        </Routes>
    );
}

export default AppRouter;
