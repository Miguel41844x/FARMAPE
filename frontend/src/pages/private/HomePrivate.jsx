
import { useAuth } from "../../context/AuthContext";

const HomePrivate = () => {
  const { user } = useAuth();

  return (
    <div style={styles.container}>
      <h1 style={styles.title}>
        Bienvenido, {user?.name || "Usuario"}
      </h1>
      <p>Aquí tienes el resumen del día</p>

      <div style={styles.cards}>
        <div style={styles.card}>
          <h3>Ventas</h3>
          <p>Revisa las ventas del día</p>
        </div>

        <div style={styles.card}>
          <h3>Productos</h3>
          <p>Gestiona tu inventario</p>
        </div>

        <div style={styles.card}>
          <h3>Reportes</h3>
          <p>Visualiza estadísticas</p>
        </div>
      </div>
    </div>
  );
};

export default HomePrivate;

const styles = {
  container: {
    padding: "30px",
    background: "#f6f8fb",
    minHeight: "100vh"
  },

  title: {
    marginBottom: "25px",
    color: "#1B7F79"
  },

  cards: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(200px, 1fr))",
    gap: "20px"
  },

  card: {
    background: "#fff",
    padding: "20px",
    borderRadius: "12px",
    boxShadow: "0 4px 10px rgba(0,0,0,0.05)",
    transition: "transform 0.2s"
  }
};