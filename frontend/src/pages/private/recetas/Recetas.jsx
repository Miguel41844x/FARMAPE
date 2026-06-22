import { useEffect, useState } from "react";
import RecetasListaTable from "../../../components/private/recetas/RecetasListaTable";
import RecetaForm from "../../../components/private/recetas/RecetaForm";
import FormulaPresupuesto from "../../../components/private/recetas/FormulaPresupuesto";
import { obtenerRecetas, registrarFormulaPresupuestada } from "../../../services/recetas/recetaService";
import "./recetas.css";

const Recetas = () => {
    const [vistaActual, setVistaActual] = useState("LISTA");
    const [recetas, setRecetas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [busqueda, setBusqueda] = useState("");
    const [paginaActual, setPaginaActual] = useState(1);
    const [recetaValidada, setRecetaValidada] = useState(null);

    const cargarRecetas = async () => {
        setLoading(true);
        setError("");
        try {
            const data = await obtenerRecetas();
            setRecetas(data);
        } catch (err) {
            setError(err.message || "No se pudieron cargar las recetas magistrales.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarRecetas();
    }, []);

    const handleValidacionFormulario = (dataFormulario) => {
        setRecetaValidada(dataFormulario);
        setVistaActual("PRESUPUESTO");
    };

    const handleFinalizarOrden = async (datosPresupuesto) => {
        setLoading(true);
        setError("");
        try {
            await registrarFormulaPresupuestada(datosPresupuesto);
            await cargarRecetas();
            setVistaActual("LISTA");
            setRecetaValidada(null);
        } catch (err) {
            setError(err.message || "No se pudo registrar la fórmula magistral.");
        } finally {
            setLoading(false);
        }
    };

    const recetasFiltradas = recetas.filter((receta) => {
        const termino = busqueda.toLowerCase();
        return (
            receta.medicoPrescriptor?.toLowerCase().includes(termino) ||
            receta.descripcionReceta?.toLowerCase().includes(termino) ||
            receta.dniPaciente?.toLowerCase().includes(termino) ||
            receta.estado?.toLowerCase().includes(termino)
        );
    });

    return (
        <div className="recetas-container" style={{ padding: "20px" }}>
            <div className="recetas-main-header" style={{ marginBottom: "20px" }}>
                <h1 style={{ fontSize: "2rem", color: "#2d3748", margin: 0 }}>Gestión de Fórmulas Magistrales</h1>
                <p style={{ margin: "4px 0 0 0", color: "#718096" }}>Módulo de validación farmacéutica, presupuesto y orden de venta</p>
            </div>

            {error && (
                <div className="receta-status-alert alert-critico">
                    <strong>Error:</strong> {error}
                </div>
            )}

            {vistaActual === "LISTA" && (
                <RecetasListaTable
                    recetas={recetasFiltradas}
                    loading={loading}
                    busqueda={busqueda}
                    setBusqueda={setBusqueda}
                    paginaActual={paginaActual}
                    setPaginaActual={setPaginaActual}
                    onRegistrarClick={() => setVistaActual("NUEVA_RECETA")}
                />
            )}

            {vistaActual === "NUEVA_RECETA" && (
                <RecetaForm
                    onValidacionExitosa={handleValidacionFormulario}
                    onCancelar={() => setVistaActual("LISTA")}
                />
            )}

            {vistaActual === "PRESUPUESTO" && (
                <FormulaPresupuesto
                    recetaData={recetaValidada}
                    onFinalizarOrden={handleFinalizarOrden}
                    onAtras={() => setVistaActual("NUEVA_RECETA")}
                />
            )}
        </div>
    );
};

export default Recetas;
