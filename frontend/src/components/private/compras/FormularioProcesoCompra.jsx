import { useState } from "react";

const configuraciones = {
    pedido: {
        titulo: "Nuevo pedido a proveedor",
        campos: [
            { name: "idProveedor", label: "Proveedor", type: "proveedor", required: true },
            { name: "fechaEntrega", label: "Fecha de entrega", type: "date", required: true },
            { name: "observaciones", label: "Observaciones", type: "textarea" },
        ],
    },
    factura: {
        titulo: "Registrar factura de proveedor",
        campos: [
            { name: "idOrdenCompra", label: "ID de orden de compra", type: "number", required: true },
            { name: "serie", label: "Serie", required: true },
            { name: "numero", label: "Número", required: true },
            { name: "fechaEmision", label: "Fecha de emisión", type: "date", required: true },
            { name: "fechaVencimiento", label: "Fecha de vencimiento", type: "date" },
            { name: "total", label: "Importe total", type: "number", step: "0.01", required: true },
            { name: "condicionPago", label: "Condición de pago", type: "select", options: ["CONTADO", "CREDITO"], required: true },
        ],
    },
    nota: {
        titulo: "Registrar nota de crédito",
        campos: [
            { name: "idFacturaProveedor", label: "ID de factura", type: "number", required: true },
            { name: "motivo", label: "Motivo", type: "select", options: ["DEVOLUCION", "DESCUENTO", "DIFERENCIA_PRECIO", "OTRO"], required: true },
            { name: "monto", label: "Monto", type: "number", step: "0.01", required: true },
            { name: "descripcion", label: "Descripción", type: "textarea", required: true },
        ],
    },
    pago: {
        titulo: "Registrar pago a proveedor",
        campos: [
            { name: "idFacturaProveedor", label: "ID de factura", type: "number", required: true },
            { name: "fechaPago", label: "Fecha de pago", type: "date", required: true },
            { name: "monto", label: "Monto pagado", type: "number", step: "0.01", required: true },
            { name: "metodoPago", label: "Método de pago", type: "select", options: ["TRANSFERENCIA", "EFECTIVO", "TARJETA"], required: true },
            { name: "referencia", label: "Número de operación o referencia" },
        ],
    },
};

const crearEstadoInicial = (campos) => campos.reduce((estado, campo) => ({
    ...estado,
    [campo.name]: campo.type === "select" ? campo.options[0] : "",
}), {});

const detalleVacio = () => ({ idProducto: "", cantidad: 1, precioUnitario: "" });

function FormularioProcesoCompra({ tipo, proveedores, productos, onCancel, onSave }) {
    const configuracion = configuraciones[tipo];
    const [form, setForm] = useState(() => crearEstadoInicial(configuracion.campos));
    const [guardando, setGuardando] = useState(false);
    const [detalles, setDetalles] = useState([detalleVacio()]);

    const manejarCambio = (event) => {
        const { name, value } = event.target;
        setForm((actual) => ({ ...actual, [name]: value }));
    };

    const manejarEnvio = async (event) => {
        event.preventDefault();
        const payload = Object.fromEntries(Object.entries(form).map(([key, value]) => {
            const campo = configuracion.campos.find((item) => item.name === key);
            return [key, campo?.type === "number" || campo?.type === "proveedor" ? Number(value) : value.trim()];
        }));

        if (tipo === "pedido") {
            payload.detalles = detalles.map((detalle) => ({
                idProducto: Number(detalle.idProducto),
                cantidad: Number(detalle.cantidad),
                precioUnitario: Number(detalle.precioUnitario),
            }));
            payload.total = totalPedido;
        }

        try {
            setGuardando(true);
            await onSave(payload);
        } catch {
            // El panel muestra el mensaje devuelto por el servicio.
        } finally {
            setGuardando(false);
        }
    };

    const manejarDetalle = (index, name, value) => {
        setDetalles((actuales) => actuales.map((detalle, posicion) => {
            if (posicion !== index) return detalle;

            if (name === "idProducto") {
                const producto = productos.find((item) => String(item.idProducto) === value);
                return {
                    ...detalle,
                    idProducto: value,
                    precioUnitario: producto?.precioCompra ?? producto?.precio ?? detalle.precioUnitario,
                };
            }

            return { ...detalle, [name]: value };
        }));
    };

    const totalPedido = detalles.reduce(
        (total, detalle) => total + Number(detalle.cantidad || 0) * Number(detalle.precioUnitario || 0),
        0
    );

    return (
        <form className="compra-form" onSubmit={manejarEnvio}>
            <header>
                <div>
                    <h2>{configuracion.titulo}</h2>
                    <p>Completa los datos requeridos para guardar el registro.</p>
                </div>
                <button type="button" onClick={onCancel} aria-label="Cerrar formulario">×</button>
            </header>

            <div className="compra-form-grid">
                {configuracion.campos.map((campo) => (
                    <label key={campo.name} className={campo.type === "textarea" ? "wide" : ""}>
                        {campo.label}
                        {campo.type === "textarea" ? (
                            <textarea name={campo.name} value={form[campo.name]} onChange={manejarCambio} required={campo.required} />
                        ) : campo.type === "select" ? (
                            <select name={campo.name} value={form[campo.name]} onChange={manejarCambio} required={campo.required}>
                                {campo.options.map((option) => <option key={option} value={option}>{option.replaceAll("_", " ")}</option>)}
                            </select>
                        ) : campo.type === "proveedor" ? (
                            <select name={campo.name} value={form[campo.name]} onChange={manejarCambio} required={campo.required}>
                                <option value="">Selecciona un proveedor</option>
                                {proveedores.map((proveedor) => (
                                    <option key={proveedor.idProveedor} value={proveedor.idProveedor}>{proveedor.razonSocial}</option>
                                ))}
                            </select>
                        ) : (
                            <input
                                name={campo.name}
                                type={campo.type || "text"}
                                step={campo.step}
                                value={form[campo.name]}
                                onChange={manejarCambio}
                                required={campo.required}
                            />
                        )}
                    </label>
                ))}
            </div>

            {tipo === "pedido" && (
                <section className="pedido-details">
                    <div className="pedido-details-header">
                        <h3>Productos del pedido</h3>
                        <button type="button" onClick={() => setDetalles((actuales) => [...actuales, detalleVacio()])}>
                            Agregar producto
                        </button>
                    </div>

                    {detalles.map((detalle, index) => (
                        <div className="pedido-detail-row" key={index}>
                            <select
                                value={detalle.idProducto}
                                onChange={(event) => manejarDetalle(index, "idProducto", event.target.value)}
                                required
                                aria-label="Producto"
                            >
                                <option value="">Selecciona un producto</option>
                                {productos.map((producto) => (
                                    <option key={producto.idProducto} value={producto.idProducto}>{producto.nombre}</option>
                                ))}
                            </select>
                            <input
                                type="number"
                                min="1"
                                value={detalle.cantidad}
                                onChange={(event) => manejarDetalle(index, "cantidad", event.target.value)}
                                required
                                aria-label="Cantidad"
                                placeholder="Cantidad"
                            />
                            <input
                                type="number"
                                min="0"
                                step="0.01"
                                value={detalle.precioUnitario}
                                onChange={(event) => manejarDetalle(index, "precioUnitario", event.target.value)}
                                required
                                aria-label="Precio unitario"
                                placeholder="Precio"
                            />
                            <button
                                type="button"
                                onClick={() => setDetalles((actuales) => actuales.filter((_, posicion) => posicion !== index))}
                                disabled={detalles.length === 1}
                                aria-label="Quitar producto"
                            >
                                ×
                            </button>
                        </div>
                    ))}

                    <p className="pedido-total"><strong>Total estimado: S/ {totalPedido.toFixed(2)}</strong></p>
                </section>
            )}

            <footer>
                <button type="button" className="secondary" onClick={onCancel}>Cancelar</button>
                <button type="submit" className="primary" disabled={guardando}>
                    {guardando ? "Guardando..." : "Guardar"}
                </button>
            </footer>
        </form>
    );
}

export default FormularioProcesoCompra;
