import { useEffect, useState } from "react";

import UsuarioForms from "../../../components/private/mantenimiento/usuarios/UsuarioForms";
import UsuariosTable from "../../../components/private/mantenimiento/usuarios/UsuariosTable.jsx";
import "./usuarios.css";

import {
    obtenerUsuarios as obtenerUsuariosService,
    actualizarEstadoTrabajador,
} from "../../../services/mantenimiento/usuarioService";

const Usuarios = () => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [usuarioEditando, setUsuarioEditando] = useState(null);

    const [usuarios, setUsuarios] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [paginaActual, setPaginaActual] = useState(1);
    const [loading, setLoading] = useState(false);

    const usuariosPorPagina = 10;

    useEffect(() => {
        cargarUsuarios();
    }, []);

    const cargarUsuarios = async () => {
        try {
            setLoading(true);

            const data = await obtenerUsuariosService();

            setUsuarios(data);
        } catch (error) {
            console.error(error);
            alert(error.message);
        } finally {
            setLoading(false);
        }
    };

    const abrirCrearUsuario = () => {
        setUsuarioEditando(null);
        setMostrarFormulario(true);
    };

    const editarUsuario = (usuario) => {
        setUsuarioEditando(usuario);
        setMostrarFormulario(true);
    };

    const eliminarUsuario = async (id) => {
        const confirmar = confirm("¿Seguro que deseas desactivar este trabajador?");
        if (!confirmar) return;

        try {
            const trabajadorActualizado = await actualizarEstadoTrabajador(
                id,
                "INACTIVO"
            );

            setUsuarios(
                usuarios.map((usuario) =>
                    usuario.idTrabajador === id ? trabajadorActualizado : usuario
                )
            );
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    const cerrarFormulario = () => {
        setMostrarFormulario(false);
        setUsuarioEditando(null);
    };

    const usuariosFiltrados = usuarios.filter((usuario) => {
        const texto = busqueda.toLowerCase();

        const dni = usuario.dni || "";
        const nombres = usuario.nombres || "";
        const apellidos = usuario.apellidos || "";
        const telefono = usuario.telefono || "";
        const direccion = usuario.direccion || "";
        const estado = usuario.estado || "";
        const rol = usuario.rol?.nombre || usuario.nombreRol || "";

        return (
            dni.toLowerCase().includes(texto) ||
            nombres.toLowerCase().includes(texto) ||
            apellidos.toLowerCase().includes(texto) ||
            telefono.toLowerCase().includes(texto) ||
            direccion.toLowerCase().includes(texto) ||
            estado.toLowerCase().includes(texto) ||
            rol.toLowerCase().includes(texto)
        );
    });

    const totalPaginas = Math.ceil(usuariosFiltrados.length / usuariosPorPagina);

    const indiceInicial = (paginaActual - 1) * usuariosPorPagina;
    const indiceFinal = indiceInicial + usuariosPorPagina;

    const usuariosPaginados = usuariosFiltrados.slice(indiceInicial, indiceFinal);

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
        <div className="usuarios-container">
            <div className="usuarios-header">
                <div>
                    <h1>Gestión de usuarios</h1>
                    <p>Crea, edita y administra usuarios del sistema.</p>
                </div>

                <button
                    className="usuarios-create-btn"
                    onClick={abrirCrearUsuario}
                >
                    Crear usuario
                </button>
            </div>

            {mostrarFormulario && (
                <div
                    className="usuarios-modal-overlay"
                    onClick={cerrarFormulario}
                >
                    <div
                        className="usuarios-modal-content"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <UsuarioForms
                            usuarioEditando={usuarioEditando}
                            cerrarFormulario={cerrarFormulario}
                            obtenerUsuarios={cargarUsuarios}
                        />
                    </div>
                </div>
            )}

            <UsuariosTable
                usuarios={usuariosPaginados}
                totalUsuarios={usuariosFiltrados.length}
                busqueda={busqueda}
                setBusqueda={cambiarBusqueda}
                loading={loading}
                paginaActual={paginaActual}
                paginaAnterior={paginaAnterior}
                paginaSiguiente={paginaSiguiente}
                onEdit={editarUsuario}
                onDelete={eliminarUsuario}
            />
        </div>
    );
};

export default Usuarios;