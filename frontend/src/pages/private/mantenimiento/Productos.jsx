import { useEffect, useState } from "react";

import ProductoForm from "../../../components/private/mantenimiento/productos/ProductoForm";
import ProductosTable from "../../../components/private/mantenimiento/productos/ProductosTable";
import "./productos.css";

import {
    obtenerProductos as obtenerProductosService,
    actualizarEstadoProducto,
} from "../../../services/mantenimiento/productoService";

const Productos = () => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [productoEditando, setProductoEditando] = useState(null);

    const [productos, setProductos] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [paginaActual, setPaginaActual] = useState(1);
    const [loading, setLoading] = useState(false);

    const productosPorPagina = 10;

    useEffect(() => {
        cargarProductos();
    }, []);

    const cargarProductos = async () => {
        try {
            setLoading(true);
            const data = await obtenerProductosService();
            setProductos(data);
        } catch (error) {
            console.error("Error al cargar productos:", error);
            alert(error.message);
        } finally {
            setLoading(false);
        }
    };

    const abrirCrearProducto = () => {
        setProductoEditando(null);
        setMostrarFormulario(true);
    };

    const editarProducto = (producto) => {
        setProductoEditando(producto);
        setMostrarFormulario(true);
    };

    const desactivarProducto = async (idProducto) => {
        const confirmar = confirm("¿Seguro que deseas desactivar este producto?");
        if (!confirmar) return;

        try {
            const productoActualizado = await actualizarEstadoProducto(
                idProducto,
                "INACTIVO"
            );

            setProductos(
                productos.map((producto) =>
                    producto.idProducto === idProducto
                        ? productoActualizado
                        : producto
                )
            );
        } catch (error) {
            console.error("Error al desactivar producto:", error);
            alert(error.message);
        }
    };

    const cerrarFormulario = () => {
        setMostrarFormulario(false);
        setProductoEditando(null);
    };

    const productosFiltrados = productos.filter((producto) => {
        const texto = busqueda.toLowerCase();

        const nombre = producto.nombre || "";
        const descripcion = producto.descripcion || "";
        const laboratorio = producto.laboratorio || "";
        const categoria =
            typeof producto.categoria === "string"
                ? producto.categoria
                : producto.categoria?.nombre || "";
        const estado = producto.estado || "";

        return (
            nombre.toLowerCase().includes(texto) ||
            descripcion.toLowerCase().includes(texto) ||
            laboratorio.toLowerCase().includes(texto) ||
            categoria.toLowerCase().includes(texto) ||
            estado.toLowerCase().includes(texto)
        );
    });

    const totalPaginas = Math.ceil(productosFiltrados.length / productosPorPagina);

    const indiceInicial = (paginaActual - 1) * productosPorPagina;
    const indiceFinal = indiceInicial + productosPorPagina;

    const productosPaginados = productosFiltrados.slice(
        indiceInicial,
        indiceFinal
    );

    const cambiarBusqueda = (valor) => {
        setBusqueda(valor);
        setPaginaActual(1);
    };

    const paginaAnterior = () => {
        if (paginaActual > 1) {
            setPaginaActual(paginaActual - 1);
        }
    };

    const paginaSiguiente = () => {
        if (paginaActual < totalPaginas) {
            setPaginaActual(paginaActual + 1);
        }
    };

    return (
        <div className="productos-container">
            <div className="productos-header">
                <div>
                    <h1>Gestión de productos</h1>
                    <p>Crea, edita y administra productos farmacéuticos.</p>
                </div>

                <button
                    className="productos-create-btn"
                    onClick={abrirCrearProducto}
                >
                    Crear producto
                </button>
            </div>

            {mostrarFormulario && (
                <div
                    className="productos-modal-overlay"
                    onClick={cerrarFormulario}
                >
                    <div
                        className="productos-modal-content"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <ProductoForm
                            productoEditando={productoEditando}
                            cerrarFormulario={cerrarFormulario}
                            obtenerProductos={cargarProductos}
                        />
                    </div>
                </div>
            )}

            <ProductosTable
                productos={productosPaginados}
                totalProductos={productosFiltrados.length}
                busqueda={busqueda}
                setBusqueda={cambiarBusqueda}
                loading={loading}
                paginaActual={paginaActual}
                totalPaginas={totalPaginas}
                paginaAnterior={paginaAnterior}
                paginaSiguiente={paginaSiguiente}
                onEdit={editarProducto}
                onDelete={desactivarProducto}
            />
        </div>
    );
};

export default Productos;
