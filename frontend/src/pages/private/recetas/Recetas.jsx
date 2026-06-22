import { useEffect, useState } from "react";
import RecetasListaTable from "../../../components/private/recetas/RecetasListaTable";
import RecetaForm from "../../../components/private/recetas/RecetaForm";
import FormulaPresupuesto from "../../../components/private/recetas/FormulaPresupuesto";
import FeedbackMessage from "../../../components/common/FeedbackMessage";
import ConfirmDialog from "../../../components/common/ConfirmDialog";
import { cambiarEstadoReceta, obtenerRecetas, registrarFormulaPresupuestada } from "../../../services/recetas/recetaService";
import "./recetas.css";

const Recetas = () => {
    const [vistaActual, setVistaActual] = useState("LISTA");
    const [recetas, setRecetas] = useState([]);
    const [loading, setLoading] = useState(true);
    const [feedback, setFeedback] = useState(null);
    const [busqueda, setBusqueda] = useState("");
    const [paginaActual, setPaginaActual] = useState(1);
    const [recetaValidada, setRecetaValidada] = useState(null);
    const [cambioEstado, setCambioEstado] = useState(null);

    const cargarRecetas = async () => {
        setLoading(true);
        try {
            const data = await obtenerRecetas();
            setRecetas(Array.isArray(data) ? data : []);
        } catch (err) {
            setFeedback({ type: "error", message: err.message || "No se pudieron cargar las recetas magistrales." });
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarRecetas();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []);

    const handleValidacionFormulario = (dataFormulario) => {
        setRecetaValidada(dataFormulario);
        setVistaActual("PRESUPUESTO");
    };

    const handleFinalizarOrden = async (datosPresupuesto) => {
        setLoading(true);
        setFeedback(null);
        try {
            const response = await registrarFormulaPresupuestada(datosPresupuesto);
            await cargarRecetas();
            setVistaActual("LISTA");
            setRecetaValidada(null);
            setFeedback({ type: "success", message: response.mensaje || "Fórmula registrada y orden enviada a caja." });
        } catch (err) {
            setFeedback({ type: "error", message: err.message || "No se pudo registrar la fórmula magistral." });
        } finally {
            setLoading(false);
        }
    };

    const confirmarCambioEstado = (receta, estado) => {
        setCambioEstado({ receta, estado });
    };

    const aplicarCambioEstado = async () => {
        if (!cambioEstado) return;
        try {
            const actualizada = await cambiarEstadoReceta(cambioEstado.receta.idReceta, cambioEstado.estado, `Cambio de estado desde pantalla de recetas a ${cambioEstado.estado}`);
            setRecetas((actuales) => actuales.map((receta) => receta.idReceta === actualizada.idReceta ? actualizada : receta));
            setFeedback({ type: "success", message: `Receta #${actualizada.idReceta} actualizada a ${actualizada.estado}.` });
        } catch (error) {
            setFeedback({ type: "error", message: error.message });
        } finally {
            setCambioEstado(null);
        }
    };

    const recetasFiltradas = recetas.filter((receta) => {
        const termino = busqueda.toLowerCase();
        return [receta.medicoPrescriptor, receta.descripcionReceta, receta.dniPaciente, receta.nombrePaciente, receta.estado, receta.estadoFormula]
            .some((valor) => String(valor ?? "").toLowerCase().includes(termino));
    });

    return (
        <div className="recetas-container">
            <div className="recetas-main-header">
                <h1>Gestión de Fórmulas Magistrales</h1>
                <p>Módulo de validación farmacéutica, presupuesto, preparación, entrega y trazabilidad de fórmula.</p>
            </div>

            {feedback && <FeedbackMessage type={feedback.type} message={feedback.message} onClose={() => setFeedback(null)} />}

            {vistaActual === "LISTA" && (
                <RecetasListaTable
                    recetas={recetasFiltradas}
                    loading={loading}
                    busqueda={busqueda}
                    setBusqueda={setBusqueda}
                    paginaActual={paginaActual}
                    setPaginaActual={setPaginaActual}
                    onRegistrarClick={() => setVistaActual("NUEVA_RECETA")}
                    onCambiarEstado={confirmarCambioEstado}
                />
            )}

            {vistaActual === "NUEVA_RECETA" && (
                <RecetaForm onValidacionExitosa={handleValidacionFormulario} onCancelar={() => setVistaActual("LISTA")} />
            )}

            {vistaActual === "PRESUPUESTO" && (
                <FormulaPresupuesto recetaData={recetaValidada} onFinalizarOrden={handleFinalizarOrden} onAtras={() => setVistaActual("NUEVA_RECETA")} />
            )}

            <ConfirmDialog
                open={Boolean(cambioEstado)}
                title="Actualizar estado de receta"
                message={`¿Deseas cambiar la receta #${cambioEstado?.receta?.idReceta || ""} a ${cambioEstado?.estado || ""}?`}
                confirmText="Actualizar"
                onConfirm={aplicarCambioEstado}
                onCancel={() => setCambioEstado(null)}
            />
        </div>
    );
};

export default Recetas;
