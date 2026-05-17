import "./homePrivate.css";
import { useAuth } from "../../context/AuthContext";
import OrdersTable from "../../components/private/homePrivate/OrdersTable";
const HomePrivate = () => {
	const { user } = useAuth();

	return (
		<div className="home-container">
			<h1 className="home-title">
				Bienvenido, {user?.name || "Usuario"}
			</h1>

			<p className="home-subtitle">
				Aquí tienes el resumen del día
			</p>

			<div className="home-cards">
				<div className="home-card">
				<h3>Ventas</h3>
				<p>Revisa las ventas del día</p>
				</div>

				<div className="home-card">
				<h3>Productos</h3>
				<p>Gestiona tu inventario</p>
				</div>

				<div className="home-card">
				<h3>Reportes</h3>
				<p>Visualiza estadísticas</p>
				</div>
			</div>
			<OrdersTable/>
		</div>
	);
};

export default HomePrivate;