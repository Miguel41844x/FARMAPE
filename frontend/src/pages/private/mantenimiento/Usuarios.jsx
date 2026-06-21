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

    const cerrarFormulario = () => {
        setMostrarFormulario(false);
        setUsuarioEditando(null);
    };

    const cargarUsuarios = async () => {
        try {
            setLoading(true);

            const data = await obtenerUsuariosService();

            setUsuarios(Array.isArray(data) ? data : []);
        } catch (error) {
            console.error(error);
            alert(error.message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        cargarUsuarios();
    }, []);

    useEffect(() => {
        const cerrarConEscape = (event) => {
            if (event.key === "Escape") cerrarFormulario();
        };

        if (mostrarFormulario) {
            document.addEventListener("keydown", cerrarConEscape);
        }

        return () => document.removeEventListener("keydown", cerrarConEscape);
    }, [mostrarFormulario]);

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

            setUsuarios((usuariosActuales) =>
                usuariosActuales.map((usuario) =>
                    usuario.idTrabajador === id
                        ? { ...usuario, ...trabajadorActualizado }
                        : usuario
                )
            );
        } catch (error) {
            console.error(error);
            alert(error.message);
        }
    };

    const usuariosFiltrados = usuarios.filter((usuario) => {
        const texto = busqueda.trim().toLocaleLowerCase("es");
        const rol = usuario.rol?.nombre || usuario.nombreRol || usuario.rol;
        const email = usuario.email || usuario.correo || usuario.cuenta?.email;

        return [
            usuario.dni,
            usuario.nombres,
            usuario.apellidos,
            usuario.telefono,
            usuario.direccion,
            usuario.usuario,
            email,
            usuario.estado,
            rol,
        ].some((valor) =>
            String(valor ?? "").toLocaleLowerCase("es").includes(texto)
        );
    });

    const totalPaginas = Math.ceil(usuariosFiltrados.length / usuariosPorPagina);
    const paginaSegura = Math.min(paginaActual, totalPaginas || 1);
    const indiceInicial = (paginaSegura - 1) * usuariosPorPagina;
    const indiceFinal = indiceInicial + usuariosPorPagina;

    const usuariosPaginados = usuariosFiltrados.slice(indiceInicial, indiceFinal);

    const cambiarBusqueda = (valor) => {
        setBusqueda(valor);
        setPaginaActual(1);
    };

    const paginaAnterior = () => {
        if (paginaSegura > 1) {
            setPaginaActual(paginaSegura - 1);
        }
    };

    const paginaSiguiente = () => {
        if (paginaSegura < totalPaginas) {
            setPaginaActual(paginaSegura + 1);
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
                paginaActual={paginaSegura}
                totalPaginas={totalPaginas}
                paginaAnterior={paginaAnterior}
                paginaSiguiente={paginaSiguiente}
                onEdit={editarUsuario}
                onDelete={eliminarUsuario}
            />
        </div>
    );
};

export default Usuarios;
