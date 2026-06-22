import { useEffect, useState } from "react";
import {
    crearProducto,
    actualizarProducto,
    obtenerCategoriasProducto,
} from "../../../../services/mantenimiento/productoService";
import "./productoForm.css";

const crearEstadoInicial = (producto = null) => ({
    idCategoria: producto?.idCategoria ?? producto?.categoria?.idCategoria ?? "",
    nombre: producto?.nombre || "",
    descripcion: producto?.descripcion || "",
    laboratorio: producto?.laboratorio || "",
    precioCompra: producto?.precioCompra ?? "",
    precioVenta: producto?.precioVenta ?? "",
    stockActual: producto?.stockActual ?? "",
    stockMinimo: producto?.stockMinimo ?? "",
    fechaVencimiento: producto?.fechaVencimiento || "",
    estado: producto?.estado || "Activo",
});

const ProductoForm = ({
    productoEditando,
    cerrarFormulario,
    obtenerProductos,
}) => {
    const [categorias, setCategorias] = useState([]);

    const [form, setForm] = useState(() => crearEstadoInicial(productoEditando));

    useEffect(() => {
        obtenerCategoriasProducto()
            .then(setCategorias)
            .catch((error) => console.error("Error al cargar categorías:", error));
    }, []);

    const manejarCambio = (e) => {
        const { name, value } = e.target;

        setForm({
            ...form,
            [name]: value,
        });
    };

    const guardarProducto = async (e) => {
        e.preventDefault();

        try {
            const request = {
                idCategoria: Number(form.idCategoria),
                nombre: form.nombre,
                descripcion: form.descripcion,
                laboratorio: form.laboratorio,
                precioCompra: Number(form.precioCompra),
                precioVenta: Number(form.precioVenta),
                stockActual: Number(form.stockActual),
                stockMinimo: Number(form.stockMinimo),
                fechaVencimiento: form.fechaVencimiento,
                estado: form.estado,
            };

            if (productoEditando) {
                await actualizarProducto(productoEditando.idProducto, request);
            } else {
                await crearProducto(request);
            }

            await obtenerProductos();
            cerrarFormulario();
        } catch (error) {
            console.error("Error al guardar producto:", error);
            alert(error.message);
        }
    };

    return (
        <form className="producto-form-container" onSubmit={guardarProducto}>
            <div className="producto-form-header">
                <div>
                    <h2>
                        {productoEditando
                            ? "Editar producto"
                            : "Registrar producto"}
                    </h2>

                    <p>
                        Complete la información del producto farmacéutico.
                    </p>
                </div>
            </div>

            <div className="producto-form-grid">
                <div className="producto-form-group">
                    <label>Categoría</label>
                    <select
                        name="idCategoria"
                        value={form.idCategoria}
                        onChange={manejarCambio}
                        required
                    >
                        <option value="">Seleccione una categoría</option>
                        {categorias.map((categoria) => (
                            <option
                                key={categoria.idCategoria}
                                value={categoria.idCategoria}
                            >
                                {categoria.nombre}
                            </option>
                        ))}
                    </select>
                </div>

                <div className="producto-form-group">
                    <label>Nombre</label>
                    <input
                        name="nombre"
                        value={form.nombre}
                        onChange={manejarCambio}
                        placeholder="Ej: Paracetamol 500mg"
                        required
                    />
                </div>

                <div className="producto-form-group">
                    <label>Laboratorio</label>
                    <input
                        name="laboratorio"
                        value={form.laboratorio}
                        onChange={manejarCambio}
                        placeholder="Ej: Medifarma"
                    />
                </div>

                <div className="producto-form-group">
                    <label>Precio compra</label>
                    <input
                        name="precioCompra"
                        type="number"
                        step="0.01"
                        value={form.precioCompra}
                        onChange={manejarCambio}
                        placeholder="0.00"
                    />
                </div>

                <div className="producto-form-group">
                    <label>Precio venta</label>
                    <input
                        name="precioVenta"
                        type="number"
                        step="0.01"
                        value={form.precioVenta}
                        onChange={manejarCambio}
                        placeholder="0.00"
                        required
                    />
                </div>

                <div className="producto-form-group">
                    <label>Stock actual</label>
                    <input
                        name="stockActual"
                        type="number"
                        value={form.stockActual}
                        onChange={manejarCambio}
                        placeholder="0"
                    />
                </div>

                <div className="producto-form-group">
                    <label>Stock mínimo</label>
                    <input
                        name="stockMinimo"
                        type="number"
                        value={form.stockMinimo}
                        onChange={manejarCambio}
                        placeholder="0"
                    />
                </div>

                <div className="producto-form-group">
                    <label>Fecha de vencimiento</label>
                    <input
                        name="fechaVencimiento"
                        type="date"
                        value={form.fechaVencimiento}
                        onChange={manejarCambio}
                    />
                </div>

                <div className="producto-form-group">
                    <label>Estado</label>
                    <select
                        name="estado"
                        value={form.estado}
                        onChange={manejarCambio}
                    >
                        <option value="Activo">Activo</option>
                        <option value="Inactivo">Inactivo</option>
                    </select>
                </div>

                <div className="producto-form-group full">
                    <label>Descripción</label>
                    <textarea
                        name="descripcion"
                        value={form.descripcion}
                        onChange={manejarCambio}
                        placeholder="Descripción del producto"
                    />
                </div>
            </div>

            <div className="producto-form-actions">
                <button
                    type="button"
                    className="cancel"
                    onClick={cerrarFormulario}
                >
                    Cancelar
                </button>

                <button type="submit">
                    {productoEditando ? "Actualizar" : "Guardar"}
                </button>
            </div>
        </form>
    );
};

export default ProductoForm;
