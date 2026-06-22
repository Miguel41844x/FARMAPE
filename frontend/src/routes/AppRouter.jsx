import { Route, Routes } from "react-router-dom";
import PrivateLayout from "../pages/private/PrivateLayout";
import PrivateRoute from "./PrivateRoute";
import PermissionRoute from "./PermissionRoute";
import { PERMISSIONS } from "../constants/permissions";

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
import Roles from "../pages/private/mantenimiento/Roles";
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
import Recetas from "../pages/private/recetas/Recetas";
import Auditoria from "../pages/private/auditoria/Auditoria";

function AppRouter() {
    return (
        <Routes>
            <Route path="/" element={<HomePublic />} />
            <Route path="/login" element={<Login />} />

            <Route element={<PrivateRoute />}>
                <Route element={<PrivateLayout />}>
                    <Route path="/homePrivate" element={<HomePrivate />} />

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.SALE_CREATE]} />}>
                        <Route path="/ventas" element={<Ventas />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.PAYMENT_READ]} />}>
                        <Route path="/caja" element={<Caja />} />
                    </Route>

                    <Route
                        element={
                            <PermissionRoute
                                permissions={[
                                    PERMISSIONS.USER_MANAGE,
                                    PERMISSIONS.ROLE_READ,
                                    PERMISSIONS.ROLE_MANAGE,
                                    PERMISSIONS.ROLE_ASSIGN,
                                ]}
                            />
                        }
                    >
                        <Route path="/mantenimiento" element={<Mantenimiento />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.USER_MANAGE]} />}>
                        <Route path="/mantenimiento/usuarios" element={<Usuarios />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.ROLE_READ, PERMISSIONS.ROLE_MANAGE, PERMISSIONS.ROLE_ASSIGN]} />}>
                        <Route path="/mantenimiento/roles" element={<Roles />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.PRODUCT_MANAGE]} />}>
                        <Route path="/productos" element={<Productos />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.PURCHASE_MANAGE]} />}>
                        <Route path="/compras-proveedores" element={<ComprasProveedores />} />
                        <Route path="/compras-proveedores/pedidos" element={<PedidosCompra />} />
                        <Route path="/compras-proveedores/facturas" element={<FacturasProveedor />} />
                        <Route path="/compras-proveedores/notas-credito" element={<NotasCredito />} />
                        <Route path="/compras-proveedores/pagos" element={<PagosCredito />} />
                        <Route path="/compras-proveedores/proveedores" element={<ProveedoresCompras />} />
                    </Route>

                    <Route
                        element={
                            <PermissionRoute
                                permissions={[
                                    PERMISSIONS.DISPATCH_MANAGE,
                                    PERMISSIONS.INVENTORY_MANAGE,
                                ]}
                            />
                        }
                    >
                        <Route path="/despacho-almacen" element={<DespachoAlmacen />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.DISPATCH_MANAGE]} />}>
                        <Route path="/despacho-almacen/entrega" element={<EntregaTienda />} />
                        <Route path="/despacho-almacen/reparto" element={<RepartoDomicilio />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.INVENTORY_MANAGE]} />}>
                        <Route path="/despacho-almacen/ingreso" element={<RegistrarIngreso />} />
                        <Route path="/despacho-almacen/verificacion" element={<VerificarProductos />} />
                        <Route path="/despacho-almacen/informes" element={<InformeAlmacen />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.FORMULA_MANAGE]} />}>
                        <Route path="/recetas" element={<Recetas />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.REPORT_VIEW]} />}>
                        <Route path="/reportes" element={<Reporte />} />
                    </Route>

                    <Route element={<PermissionRoute permissions={[PERMISSIONS.AUDIT_VIEW]} />}>
                        <Route path="/auditoria" element={<Auditoria />} />
                    </Route>
                </Route>
            </Route>
        </Routes>
    );
}

export default AppRouter;
