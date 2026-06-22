import "./homePrivate.css";
import { useAuth } from "../../context/AuthContext";
import { NavLink } from "react-router-dom";
import { PERMISSIONS } from "../../constants/permissions";
import OrdersTable from "../../components/private/homePrivate/OrdersTable";
import CashSummary from "../../components/private/homePrivate/CashSummary";

const accesos = [
    { permiso: PERMISSIONS.SALE_CREATE, titulo: "Ventas", descripcion: "Registrar una nueva orden", ruta: "/ventas" },
    { permiso: PERMISSIONS.PAYMENT_READ, titulo: "Caja", descripcion: "Consultar órdenes por cobrar", ruta: "/caja" },
    { permiso: PERMISSIONS.USER_MANAGE, titulo: "Usuarios", descripcion: "Administrar cuentas y roles", ruta: "/mantenimiento" },
    { permiso: PERMISSIONS.PRODUCT_MANAGE, titulo: "Productos", descripcion: "Administrar el catálogo", ruta: "/productos" },
    { permiso: PERMISSIONS.PURCHASE_MANAGE, titulo: "Compras", descripcion: "Gestionar proveedores y compras", ruta: "/compras-proveedores" },
    { permiso: PERMISSIONS.INVENTORY_MANAGE, titulo: "Almacén", descripcion: "Controlar ingresos e inventario", ruta: "/despacho-almacen" },
    { permiso: PERMISSIONS.DISPATCH_MANAGE, titulo: "Despacho", descripcion: "Gestionar entregas", ruta: "/despacho-almacen" },
    { permiso: PERMISSIONS.REPORT_VIEW, titulo: "Reportes", descripcion: "Consultar indicadores", ruta: "/reportes" },
];

const contextos = [
    { permiso: PERMISSIONS.SALE_READ, titulo: "Panel de ventas", descripcion: "Registra pedidos y consulta la actividad reciente.", contenido: "ventas" },
    { permiso: PERMISSIONS.PAYMENT_READ, titulo: "Panel de caja", descripcion: "Revisa y procesa las órdenes pendientes de pago.", contenido: "caja" },
    { permiso: PERMISSIONS.USER_MANAGE, titulo: "Administración del sistema", descripcion: "Gestiona usuarios, cuentas y acceso al sistema.", contenido: "administracion" },
    { permiso: PERMISSIONS.ROLE_MANAGE, titulo: "Administración de accesos", descripcion: "Configura roles y permisos del sistema.", contenido: "administracion" },
    { permiso: PERMISSIONS.INVENTORY_MANAGE, titulo: "Panel de almacén", descripcion: "Controla productos, lotes, ingresos y existencias.", contenido: "almacen" },
    { permiso: PERMISSIONS.DISPATCH_MANAGE, titulo: "Panel de despacho", descripcion: "Gestiona entregas en tienda y reparto a domicilio.", contenido: "despacho" },
    { permiso: PERMISSIONS.PURCHASE_MANAGE, titulo: "Panel de compras", descripcion: "Administra proveedores, pedidos y facturas.", contenido: "compras" },
    { permiso: PERMISSIONS.FORMULA_MANAGE, titulo: "Panel farmacéutico", descripcion: "Gestiona recetas y preparaciones magistrales.", contenido: "recetas" },
    { permiso: PERMISSIONS.REPORT_VIEW, titulo: "Panel gerencial", descripcion: "Consulta indicadores e informes para la toma de decisiones.", contenido: "reportes" },
];

const HomePrivate = () => {
	const { user, hasPermission } = useAuth();
	const accesosVisibles = accesos.filter((acceso) => hasPermission(acceso.permiso));
	const contexto = contextos.find((item) => hasPermission(item.permiso));
	const fecha = new Intl.DateTimeFormat("es-PE", { dateStyle: "full" }).format(new Date());

	return (
		<div className="home-container">
			<h1 className="home-title">{contexto?.titulo || `Bienvenido, ${user?.nombres}`}</h1>

			<p className="home-subtitle">
				{contexto?.descripcion || "Bienvenido al sistema"} {fecha}. Sesión: {user?.rol}.
			</p>

			<div className="home-cards">
				{accesosVisibles.map((acceso) => (
					<NavLink className="home-card" to={acceso.ruta} key={acceso.permiso}>
						<h3>{acceso.titulo}</h3>
						<p>{acceso.descripcion}</p>
					</NavLink>
				))}
			</div>

			{accesosVisibles.length === 0 && (
				<p className="home-subtitle">Tu cuenta no tiene módulos operativos asignados. Contacta al administrador.</p>
			)}

			{contexto?.contenido === "ventas" && <OrdersTable />}
			{contexto?.contenido === "caja" && <CashSummary />}
		</div>
	);
};

export default HomePrivate;
