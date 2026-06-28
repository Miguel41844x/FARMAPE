import { useState } from "react";
import {
    crearProveedor,
    actualizarProveedor,
} from "../../../../services/compras/proveedorService";
import "./proveedorForm.css";

const crearEstadoInicial = (proveedor) => ({
    ruc: proveedor?.ruc || "",
    razonSocial: proveedor?.razonSocial || "",
    telefono: proveedor?.telefono || "",
    email: proveedor?.email || "",
    direccion: proveedor?.direccion || "",
    tipoProveedor: proveedor?.tipoProveedor || "Distribuidor",
});

const ProveedorForm = ({
    proveedorEditando,
    cerrarFormulario,
    obtenerProveedores,
}) => {
    const [form, setForm] = useState(() => crearEstadoInicial(proveedorEditando));
    const [guardando, setGuardando] = useState(false);

    const manejarCambio = (e) => {
        const { name, value } = e.target;

        setForm({
            ...form,
            [name]: value,
        });
    };

    const guardarProveedor = async (e) => {
        e.preventDefault();

        if (!/^\d{11}$/.test(form.ruc.trim())) {
            alert("El RUC debe contener exactamente 11 dígitos");
            return;
        }
        if (!form.razonSocial.trim()) {
            alert("La razón social es obligatoria");
            return;
        }

        try {
            setGuardando(true);
            if (proveedorEditando) {
                await actualizarProveedor(proveedorEditando.idProveedor, form);
            } else {
                await crearProveedor(form);
            }

            await obtenerProveedores();
            cerrarFormulario();
        } catch (error) {
            console.error("Error al guardar proveedor:", error);
            alert(error.message);
        } finally {
            setGuardando(false);
        }
    };

    return (
        <form className="proveedor-form-container" onSubmit={guardarProveedor}>
            <div className="proveedor-form-header">
                <div>
                    <h2>
                        {proveedorEditando
                            ? "Editar proveedor"
                            : "Registrar proveedor"}
                    </h2>

                    <p>Complete la información comercial del proveedor.</p>
                </div>

                <button
                    type="button"
                    className="proveedor-close-btn"
                    onClick={cerrarFormulario}
                >
                    ×
                </button>
            </div>

            <div className="proveedor-form-grid">
                <div className="proveedor-form-group">
                    <label>RUC</label>
                    <input
                        name="ruc"
                        value={form.ruc}
                        onChange={manejarCambio}
                        placeholder="Ej: 20123456789"
                        maxLength="11"
                        minLength="11"
                        inputMode="numeric"
                        pattern="\d{11}"
                        title="Ingrese un RUC de 11 dígitos"
                        required
                    />
                </div>

                <div className="proveedor-form-group">
                    <label>Razón social</label>
                    <input
                        name="razonSocial"
                        value={form.razonSocial}
                        onChange={manejarCambio}
                        placeholder="Ej: Distribuidora Farma SAC"
                        maxLength={150}
                        required
                    />
                </div>

                <div className="proveedor-form-group">
                    <label>Teléfono</label>
                    <input
                        name="telefono"
                        value={form.telefono}
                        onChange={manejarCambio}
                        placeholder="Ej: 014567890"
                        maxLength={20}
                        inputMode="tel"
                    />
                </div>

                <div className="proveedor-form-group">
                    <label>Email</label>
                    <input
                        name="email"
                        type="email"
                        value={form.email}
                        onChange={manejarCambio}
                        placeholder="Ej: ventas@farma.com"
                        maxLength={100}
                    />
                </div>

                <div className="proveedor-form-group">
                    <label>Tipo de proveedor</label>
                    <select
                        name="tipoProveedor"
                        value={form.tipoProveedor}
                        onChange={manejarCambio}
                    >
                        <option value="Distribuidor">Distribuidor</option>
                        <option value="Laboratorio">Laboratorio</option>
                        <option value="Proveedor">Proveedor</option>
                    </select>
                </div>

                <div className="proveedor-form-group full">
                    <label>Dirección</label>
                    <textarea
                        name="direccion"
                        value={form.direccion}
                        onChange={manejarCambio}
                        maxLength={200}
                        placeholder="Ej: Av. Industrial 100, Lima"
                    />
                </div>
            </div>

            <div className="proveedor-form-actions">
                <button
                    type="button"
                    className="cancel"
                    onClick={cerrarFormulario}
                >
                    Cancelar
                </button>

                <button type="submit" disabled={guardando}>
                    {guardando ? "Guardando..." : proveedorEditando ? "Actualizar" : "Guardar"}
                </button>
            </div>
        </form>
    );
};

export default ProveedorForm;
