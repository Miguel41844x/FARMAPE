import { useEffect, useState } from "react";

import UsuarioForms from "../../../components/private/mantenimiento/usuarios/UsuarioForms";
import UsuariosTable from "../../../components/private/mantenimiento/usuarios/UsuariosTable.jsx";
import "./usuarios.css";

const Usuarios = () => {
    const [mostrarFormulario, setMostrarFormulario] = useState(false);
    const [usuarioEditando, setUsuarioEditando] = useState(null);

    const [usuarios, setUsuarios] = useState([]);
    const [busqueda, setBusqueda] = useState("");
    const [paginaActual, setPaginaActual] = useState(1);
    const [loading, setLoading] = useState(false);

    const usuariosPorPagina = 10;

    useEffect(() => {
        obtenerUsuarios();
    }, []);

    const obtenerUsuarios = async () => {
        try {
            setLoading(true);

            const toker = localStorage.getItem("token");

            const response = await fetch("http://localhost:8080/api/usuarios", {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok){
                throw new Error("Error al obtener usuarios");
            }

            const data = await response.json();

            setUsuarios(data);

        } catch(error){
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
        const confirmar = confirm("¿Seguro que deseas eliminar este usuario?")

        if(!confirmar) return;

        try{
            const token = localStorage.getItem("token");

             const response = await fetch(`http://localhost:8080/api/usuarios/${id}`, {
                method: "DELETE",
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            if (!response.ok) {
                throw new Error("No se pudo eliminar el usuario");
            }

            setUsuarios(usuarios.filter((usuario) => usuario.id !== id));
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

        return (
            usuario.dni?.toLowerCase().includes(texto) ||
            usuario.nombres?.toLowerCase().includes(texto) ||
            usuario.apellidos?.toLowerCase().includes(texto) ||
            usuario.usuario?.toLowerCase().includes(texto) ||
            usuario.email?.toLowerCase().includes(texto) ||
            usuario.rol?.toLowerCase().includes(texto)
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
                            obtenerUsuarios={obtenerUsuarios}
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