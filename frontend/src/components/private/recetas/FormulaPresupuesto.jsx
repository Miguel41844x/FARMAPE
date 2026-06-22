import { useEffect, useState } from "react";
import { obtenerInsumosDisponibles } from "../../../services/recetas/recetaService";
import "./formulaPresupuesto.css";

const FormulaPresupuesto = ({ recetaData, onFinalizarOrden, onAtras }) => {
    const [insumosDisponibles, setInsumosDisponibles] = useState([]);
    const [carritoInsumos, setCarritoInsumos] = useState([]);
    const [manoObra, setManoObra] = useState(15.00);
    const [insumoSeleccionado, setInsumoSeleccionado] = useState("");
    const [cantidadInsumo, setCantidadInsumo] = useState(1);
    const [unidadMedida, setUnidadMedida] = useState("unidad");
    const [errorStock, setErrorStock] = useState("");
    const [loadingInsumos, setLoadingInsumos] = useState(true);

    useEffect(() => {
        const cargarInsumos = async () => {
            setLoadingInsumos(true);
            try {
                const data = await obtenerInsumosDisponibles();
                setInsumosDisponibles(data);
            } catch (error) {
                setErrorStock(error.message || "No se pudieron cargar los insumos disponibles.");
            } finally {
                setLoadingInsumos(false);
            }
        };

        cargarInsumos();
    }, []);

    const subtotalInsumos = carritoInsumos.reduce(
        (acc, item) => acc + (Number(item.precioUnitario || 0) * Number(item.cantidad || 0)),
        0
    );
    const costoTotal = subtotalInsumos + Number(manoObra || 0);

    const handleAgregarInsumo = () => {
        if (!insumoSeleccionado) return;

        const insumo = insumosDisponibles.find(i => i.idProducto === Number(insumoSeleccionado));
        if (!insumo) return;

        const cantidad = Number(cantidadInsumo || 0);
        if (cantidad <= 0) {
            setErrorStock("La cantidad debe ser mayor a 0.");
            return;
        }

        if (cantidad > Number(insumo.stockActual || 0)) {
            setErrorStock(`Stock insuficiente. Solo quedan ${insumo.stockActual || 0} unidades de ${insumo.nombre}.`);
            return;
        }

        setErrorStock("");
        const existe = carritoInsumos.find(item => item.idProducto === insumo.idProducto);

        if (existe) {
            const nuevaCantidad = Number(existe.cantidad) + cantidad;
            if (nuevaCantidad > Number(insumo.stockActual || 0)) {
                setErrorStock(`No se puede agregar más. Supera el stock máximo de ${insumo.stockActual || 0}.`);
                return;
            }
            setCarritoInsumos(carritoInsumos.map(item =>
                item.idProducto === insumo.idProducto
                    ? { ...item, cantidad: nuevaCantidad, unidadMedida }
                    : item
            ));
        } else {
            setCarritoInsumos([
                ...carritoInsumos,
                {
                    ...insumo,
                    cantidad,
                    unidadMedida,
                    precioUnitario: Number(insumo.precioVenta || insumo.precioCompra || 0),
                },
            ]);
        }

        setInsumoSeleccionado("");
        setCantidadInsumo(1);
        setUnidadMedida("unidad");
    };

    const handleEliminarInsumo = (idProducto) => {
        setCarritoInsumos(carritoInsumos.filter(item => item.idProducto !== idProducto));
    };

    const handleGenerarOrdenVenta = () => {
        if (carritoInsumos.length === 0) {
            alert("Debe agregar al menos un insumo para costear la fórmula magistral.");
            return;
        }

        onFinalizarOrden({
            ...recetaData,
            presupuesto: Number(costoTotal.toFixed(2)),
            descripcionFormula: recetaData?.tipoFormula || recetaData?.diagnostico || "Fórmula magistral",
            instruccionesUso: "Usar según indicación médica y orientación del químico farmacéutico.",
            insumos: carritoInsumos.map(item => ({
                idProducto: item.idProducto,
                cantidad: Number(item.cantidad),
                unidadMedida: item.unidadMedida || "unidad",
            })),
        });
    };

    return (
        <div className="presupuesto-container">
            <div className="presupuesto-header">
                <h2>Presupuestar y Elaborar Fórmula Magistral</h2>
                <p>
                    Módulo de costeo de insumos químicos para la receta del paciente:{" "}
                    <strong>{recetaData?.nombrePaciente || "Paciente General"}</strong>
                </p>
            </div>

            <div className="receta-resumen-card">
                <div className="resumen-grid">
                    <div><strong>Médico Prescriptor:</strong> {recetaData?.medicoPrescriptor}</div>
                    <div><strong>Diagnóstico:</strong> {recetaData?.diagnostico}</div>
                    <div className="resumen-full">
                        <strong>Fórmula Solicitada:</strong>
                        <span className="insumos-solicitados-render">
                            {recetaData?.componentes?.length > 0 ? (
                                recetaData.componentes.map(c => `${c.nombre_insumo} (${c.cantidad_usada} ${c.unidad_medida})`).join(", ")
                            ) : (
                                <span className="no-insumos-placeholder">Ningún insumo especificado en la receta...</span>
                            )}
                        </span>
                    </div>
                </div>
            </div>

            <div className="presupuesto-workspace">
                <div className="workspace-panel-left">
                    <h3>Selección de Insumos y Reactivos</h3>

                    <div className="insumo-picker-row">
                        <div className="select-field">
                            <label>Insumo Químico / Base</label>
                            <select
                                value={insumoSeleccionado}
                                onChange={(e) => setInsumoSeleccionado(e.target.value)}
                                disabled={loadingInsumos}
                            >
                                <option value="">{loadingInsumos ? "Cargando insumos..." : "-- Seleccione Insumo --"}</option>
                                {insumosDisponibles.map(i => (
                                    <option key={i.idProducto} value={i.idProducto}>
                                        {i.nombre} - Stock: {i.stockActual || 0} | S/. {Number(i.precioVenta || i.precioCompra || 0).toFixed(2)}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="quantity-field">
                            <label>Cantidad requerida</label>
                            <input
                                type="number"
                                min="0.1"
                                step="0.1"
                                value={cantidadInsumo}
                                onChange={(e) => setCantidadInsumo(e.target.value)}
                            />
                        </div>

                        <div className="quantity-field">
                            <label>Unidad</label>
                            <select value={unidadMedida} onChange={(e) => setUnidadMedida(e.target.value)}>
                                <option value="unidad">unidad</option>
                                <option value="mg">mg</option>
                                <option value="g">g</option>
                                <option value="ml">ml</option>
                            </select>
                        </div>

                        <button type="button" className="btn-add-insumo" onClick={handleAgregarInsumo}>
                            + Añadir
                        </button>
                    </div>

                    {errorStock && <div className="insumo-stock-error">⚠️ {errorStock}</div>}

                    <table className="tabla-insumos-presupuesto">
                        <thead>
                            <tr>
                                <th>Insumo</th>
                                <th>Precio Unitario</th>
                                <th>Cantidad</th>
                                <th>Subtotal</th>
                                <th>Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            {carritoInsumos.length === 0 ? (
                                <tr>
                                    <td colSpan="5" className="no-insumos">No se han añadido insumos al presupuesto todavía.</td>
                                </tr>
                            ) : (
                                carritoInsumos.map(item => (
                                    <tr key={item.idProducto}>
                                        <td>{item.nombre}</td>
                                        <td>S/. {Number(item.precioUnitario || 0).toFixed(2)}</td>
                                        <td>{item.cantidad} {item.unidadMedida}</td>
                                        <td>S/. {(Number(item.precioUnitario || 0) * Number(item.cantidad || 0)).toFixed(2)}</td>
                                        <td>
                                            <button className="btn-delete-insumo" onClick={() => handleEliminarInsumo(item.idProducto)}>
                                                Eliminar
                                            </button>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>

                <div className="workspace-panel-right">
                    <h3>Resumen del Presupuesto</h3>

                    <div className="summary-row">
                        <span>Total Insumos Químicos:</span>
                        <strong>S/. {subtotalInsumos.toFixed(2)}</strong>
                    </div>

                    <div className="summary-row">
                        <label>Costo de Elaboración (Q.F.):</label>
                        <input
                            type="number"
                            value={manoObra}
                            onChange={(e) => setManoObra(e.target.value)}
                        />
                    </div>

                    <div className="summary-total-box">
                        <span>PRESUPUESTO TOTAL:</span>
                        <h2>S/. {costoTotal.toFixed(2)}</h2>
                    </div>

                    <div className="panel-right-actions">
                        <button className="btn-back-flow" onClick={onAtras}>
                            Modificar Datos
                        </button>
                        <button
                            className="btn-submit-orden"
                            onClick={handleGenerarOrdenVenta}
                            disabled={carritoInsumos.length === 0}
                        >
                            Generar Orden de Venta
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default FormulaPresupuesto;
